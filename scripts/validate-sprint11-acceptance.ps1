param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$MailHogUrl = "http://localhost:8025",
    [string]$AdminEmail = "admin@sindicato.es",
    [string[]]$AdminPasswordCandidates = @("Admin#12345A", "Admin@123")
)

$ErrorActionPreference = "Stop"

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw "ASSERT_FAIL: $Message"
    }
}

function Invoke-Json {
    param(
        [ValidateSet("Get", "Post", "Put", "Delete")]
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )

    if ($null -eq $Body) {
        return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers
    }

    $json = $Body | ConvertTo-Json -Depth 20 -Compress
    return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -Body $json -ContentType "application/json"
}

function Login-Admin {
    foreach ($password in $AdminPasswordCandidates) {
        try {
            $login = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/auth/login" -Body @{
                email = $AdminEmail
                password = $password
            }

            if (-not [string]::IsNullOrWhiteSpace($login.accessToken)) {
                return $login
            }
        }
        catch {
            # Try the next known local bootstrap password.
        }
    }

    throw "No se pudo autenticar admin con las passwords locales conocidas."
}

function Get-MailHogMessages {
    return Invoke-Json -Method Get -Url "$MailHogUrl/api/v2/messages"
}

function Get-MailFor {
    param([string]$Email)

    $messages = Get-MailHogMessages
    return @($messages.items | Where-Object { ($_.Content.Headers.To -join ",") -like "*$Email*" })
}

function Extract-TemporaryPassword {
    param([string]$Body)

    $match = [regex]::Match($Body, "Password temporal:\s*(\S+)")
    Assert-True -Condition $match.Success -Message "No se pudo extraer password temporal del correo."
    return $match.Groups[1].Value
}

function New-News {
    param(
        [hashtable]$Headers,
        [long]$SourceId,
        [string]$Title,
        [string]$Slug,
        [string]$Summary,
        [string]$Content
    )

    return Invoke-Json -Method Post -Url "$BaseUrl/api/v1/news" -Headers $Headers -Body @{
        sourceId = $SourceId
        title = $Title
        url = "https://validacion.local/$Slug"
        summary = $Summary
        content = $Content
        publishedAt = (Get-Date).ToUniversalTime().ToString("o")
    }
}

Write-Host "STEP health"
$health = Invoke-Json -Method Get -Url "$BaseUrl/api/v1/health"
Assert-True -Condition ($health.status -eq "UP") -Message "Backend health no es UP."

Write-Host "STEP clean MailHog"
Invoke-Json -Method Delete -Url "$MailHogUrl/api/v1/messages" | Out-Null

Write-Host "STEP admin login"
$adminLogin = Login-Admin
$headers = @{ Authorization = "Bearer $($adminLogin.accessToken)" }

Write-Host "STEP sources"
$sources = @((Invoke-Json -Method Get -Url "$BaseUrl/api/v1/sources" -Headers $headers) | Select-Object -First 1000)
Assert-True -Condition ($sources.Count -gt 0) -Message "No hay fuentes disponibles."
$source = $sources | Select-Object -First 1
$sourceId = [long]$source.id

Write-Host "STEP user management and MailHog notifications"
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$userEmail = "acceptance.$stamp@sindicato.es"
$createdUser = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/users" -Headers $headers -Body @{
    email = $userEmail
    name = "Acceptance Editor"
    role = "EDITOR"
}
Assert-True -Condition ($createdUser.email -eq $userEmail) -Message "Usuario creado no coincide."
Assert-True -Condition ($createdUser.status -eq "PENDING_ACTIVATION") -Message "Usuario no queda PENDING_ACTIVATION."

Start-Sleep -Milliseconds 1200
$newUserMail = Get-MailFor -Email $userEmail | Select-Object -First 1
Assert-True -Condition ($null -ne $newUserMail) -Message "MailHog no contiene correo de password temporal."
$temporaryPassword = Extract-TemporaryPassword -Body $newUserMail.Content.Body

$temporaryLogin = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/auth/login" -Body @{
    email = $userEmail
    password = $temporaryPassword
}
Assert-True -Condition ($temporaryLogin.user.mustChangePassword -eq $true) -Message "Primer login no fuerza cambio de password."
$temporaryHeaders = @{ Authorization = "Bearer $($temporaryLogin.accessToken)" }

$newPassword = "Acceptance#12345A"
$changePassword = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/auth/change-password" -Headers $temporaryHeaders -Body @{
    currentPassword = $temporaryPassword
    newPassword = $newPassword
}
Assert-True -Condition ($changePassword.message -like "Password cambiada*") -Message "Cambio de password no confirmado."

$changedLogin = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/auth/login" -Body @{
    email = $userEmail
    password = $newPassword
}
Assert-True -Condition ($changedLogin.user.mustChangePassword -eq $false) -Message "Usuario sigue forzado a cambiar password tras cambiarla."

Invoke-Json -Method Post -Url "$BaseUrl/api/v1/users/$($createdUser.id)/lock" -Headers $headers -Body @{} | Out-Null
Invoke-Json -Method Post -Url "$BaseUrl/api/v1/users/$($createdUser.id)/unlock" -Headers $headers -Body @{} | Out-Null
Invoke-Json -Method Post -Url "$BaseUrl/api/v1/users/$($createdUser.id)/disable" -Headers $headers -Body @{} | Out-Null

Start-Sleep -Milliseconds 1200
$userMails = Get-MailFor -Email $userEmail
Assert-True -Condition ($userMails.Count -ge 4) -Message "Faltan notificaciones MailHog de usuario."

Write-Host "STEP news -> classification -> events"
$newsA = New-News -Headers $headers -SourceId $sourceId `
    -Title "Acceptance oposiciones docentes extraordinarias $stamp" `
    -Slug "acceptance-oposiciones-$stamp" `
    -Summary "Convocatoria sindical sobre oposiciones docentes extraordinarias." `
    -Content "La noticia trata exclusivamente sobre oposiciones docentes y acceso a empleo publico."
$newsB = New-News -Headers $headers -SourceId $sourceId `
    -Title "Acceptance ratios escolares y plantillas $stamp" `
    -Slug "acceptance-ratios-$stamp" `
    -Summary "Debate sobre ratios escolares, plantillas docentes y condiciones laborales." `
    -Content "La noticia trata sobre ratios escolares y aumento de plantillas en centros educativos."

$classificationA = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/classifications/classify" -Headers $headers -Body @{ newsId = $newsA.id }
$classificationB = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/classifications/classify" -Headers $headers -Body @{ newsId = $newsB.id }
Assert-True -Condition ($classificationA.newsId -eq $newsA.id) -Message "Clasificacion A incorrecta."
Assert-True -Condition ($classificationB.newsId -eq $newsB.id) -Message "Clasificacion B incorrecta."

$eventA = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/events/detect" -Headers $headers -Body @{ newsId = $newsA.id }
$eventB = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/events/detect" -Headers $headers -Body @{ newsId = $newsB.id }
Assert-True -Condition ($eventA.eventId -gt 0) -Message "Evento A no generado."
Assert-True -Condition ($eventB.eventId -gt 0) -Message "Evento B no generado."

if ($eventA.eventId -ne $eventB.eventId) {
    $merged = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/events/merge" -Headers $headers -Body @{
        targetEventId = $eventA.eventId
        sourceEventIds = @($eventB.eventId)
    }
    Assert-True -Condition ($merged.id -eq $eventA.eventId) -Message "Merge no devuelve evento target."
}

Write-Host "STEP analysis -> content -> edit -> approve"
$analysis = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/analysis/generate" -Headers $headers -Body @{ eventId = $eventA.eventId }
Assert-True -Condition ($analysis.eventId -eq $eventA.eventId) -Message "Analisis no asociado al evento."

$content = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/content/generate" -Headers $headers -Body @{
    eventId = $eventA.eventId
    analysisId = $analysis.id
    channel = "TELEGRAM"
    tone = "INFORMATIVO"
    length = "SHORT"
}
Assert-True -Condition ($content.eventId -eq $eventA.eventId) -Message "Contenido no asociado al evento."

$edited = Invoke-Json -Method Put -Url "$BaseUrl/api/v1/content/$($content.id)" -Headers $headers -Body @{
    title = "Acceptance contenido editado $stamp"
    content = "Contenido editado durante la aceptacion Sprint 11."
    tone = "INFORMATIVO"
}
Assert-True -Condition ($edited.status -eq "PENDING_REVIEW") -Message "Contenido editado no vuelve a PENDING_REVIEW."

$approved = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/content/$($content.id)/approve" -Headers $headers -Body @{}
Assert-True -Condition ($approved.status -eq "APPROVED") -Message "Contenido no queda APPROVED."

Write-Host "STEP publication and scheduling"
$scheduled = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/publications/$($content.id)/schedule" -Headers $headers -Body @{
    scheduledAt = (Get-Date).ToUniversalTime().AddDays(1).ToString("o")
}
Assert-True -Condition ($scheduled.status -eq "SCHEDULED") -Message "Publicacion programada no queda SCHEDULED."

$published = Invoke-Json -Method Post -Url "$BaseUrl/api/v1/publications/$($content.id)/publish" -Headers $headers -Body @{}
Assert-True -Condition ($published.status -eq "PUBLISHED") -Message "Publicacion inmediata no queda PUBLISHED. Configure Telegram fake/local antes de ejecutar."

Write-Host "STEP dashboard and audit"
$dashboard = Invoke-Json -Method Get -Url "$BaseUrl/api/v1/dashboard" -Headers $headers
Assert-True -Condition ($null -ne $dashboard.metricCards) -Message "Dashboard sin metricCards."

$userAudit = @(Invoke-Json -Method Get -Url "$BaseUrl/api/v1/audit/users?limit=20" -Headers $headers)
$editorialAudit = @(Invoke-Json -Method Get -Url "$BaseUrl/api/v1/audit/editorial?limit=20" -Headers $headers)
Assert-True -Condition ($userAudit.Count -gt 0) -Message "Auditoria de usuarios vacia."
Assert-True -Condition ($editorialAudit.Count -gt 0) -Message "Auditoria editorial vacia."

Write-Host ""
Write-Host "SPRINT11_ACCEPTANCE_OK userId=$($createdUser.id) eventId=$($eventA.eventId) contentId=$($content.id) publicationId=$($published.id) scheduledPublicationId=$($scheduled.id)"
