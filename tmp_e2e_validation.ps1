$ErrorActionPreference = 'Stop'

$base = 'http://localhost:8080'

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw "ASSERT_FAIL: $Message"
    }
}

function Post-Json {
    param(
        [string]$Url,
        [hashtable]$Body,
        [hashtable]$Headers = @{}
    )

    $json = $Body | ConvertTo-Json -Depth 10 -Compress
    return Invoke-RestMethod -Method Post -Uri $Url -Body $json -ContentType 'application/json' -Headers $Headers
}

function Put-Json {
    param(
        [string]$Url,
        [hashtable]$Body,
        [hashtable]$Headers = @{}
    )

    $json = $Body | ConvertTo-Json -Depth 10 -Compress
    return Invoke-RestMethod -Method Put -Uri $Url -Body $json -ContentType 'application/json' -Headers $Headers
}

Invoke-RestMethod -Method Delete -Uri 'http://localhost:8025/api/v1/messages' | Out-Null

$adminLogin = Post-Json -Url "$base/api/v1/auth/login" -Body @{ email = 'admin@sindicato.es'; password = 'Admin#12345A' }
Assert-True -Condition ($null -ne $adminLogin.accessToken) -Message 'login admin sin accessToken'
$authHeader = @{ Authorization = "Bearer $($adminLogin.accessToken)" }

$e2eEmail = "e2e.$([DateTimeOffset]::UtcNow.ToUnixTimeSeconds())@sindicato.es"
$createResp = Post-Json -Url "$base/api/v1/users" -Body @{
    email = $e2eEmail
    name = 'E2E Usuario'
    role = 'EDITOR'
    password = 'Initial#123A'
} -Headers $authHeader
Assert-True -Condition ($createResp.email -eq $e2eEmail) -Message 'email creado no coincide'
$userId = [int64]$createResp.id

$users = Invoke-RestMethod -Method Get -Uri "$base/api/v1/users" -Headers $authHeader
$userList = @($users)
$found = $userList | Where-Object { [int64]$_.id -eq $userId }
Assert-True -Condition (@($found).Count -ge 1) -Message 'usuario creado no aparece en listado'

$updated = Put-Json -Url "$base/api/v1/users/$userId" -Body @{
    name = 'E2E Usuario Editado'
    role = 'EDITOR'
    active = $true
} -Headers $authHeader
Assert-True -Condition ($updated.name -eq 'E2E Usuario Editado') -Message 'edicion de usuario fallo'

$forgot = Post-Json -Url "$base/api/v1/auth/forgot-password" -Body @{ email = $e2eEmail }
Assert-True -Condition ($forgot.message -like 'Si el email existe*') -Message 'mensaje forgot no esperado'

Start-Sleep -Milliseconds 1500
$msgs = Invoke-RestMethod -Method Get -Uri 'http://localhost:8025/api/v2/messages'
Assert-True -Condition ($msgs.total -ge 1) -Message 'MailHog sin mensajes'
$target = $msgs.items | Where-Object { ($_.Content.Headers.To -join ',') -like "*$e2eEmail*" } | Select-Object -First 1
Assert-True -Condition ($null -ne $target) -Message 'no se encontro correo al usuario E2E'
$body = $target.Content.Body
$tokenMatch = [regex]::Match($body, 'token=([a-f0-9\-]{36})')
Assert-True -Condition $tokenMatch.Success -Message 'no se pudo extraer token del correo'
$token = $tokenMatch.Groups[1].Value

$reset = Post-Json -Url "$base/api/v1/auth/reset-password" -Body @{ token = $token; newPassword = 'Nuevo#12345A' }
Assert-True -Condition ($reset.message -like 'Password actualizada*') -Message 'mensaje reset no esperado'

$loginNew = Post-Json -Url "$base/api/v1/auth/login" -Body @{ email = $e2eEmail; password = 'Nuevo#12345A' }
Assert-True -Condition ($null -ne $loginNew.accessToken) -Message 'login con password nueva fallo'

$disabled = Post-Json -Url "$base/api/v1/users/$userId/disable" -Body @{} -Headers $authHeader
Assert-True -Condition ($disabled.active -eq $false) -Message 'desactivacion no aplicada'

$failedInactive = $false
try {
    Post-Json -Url "$base/api/v1/auth/login" -Body @{ email = $e2eEmail; password = 'Nuevo#12345A' } | Out-Null
} catch {
    if ($_.Exception.Message -match '401|Unauthorized|unauthorized') {
        $failedInactive = $true
    }
}
Assert-True -Condition $failedInactive -Message 'usuario inactivo pudo iniciar sesion'

Write-Output "E2E_OK userId=$userId email=$e2eEmail token=$token"
