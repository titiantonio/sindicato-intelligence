$ErrorActionPreference = 'Stop'

Invoke-RestMethod -Method Delete -Uri 'http://localhost:8025/api/v1/messages' | Out-Null

$forgotBody = @{ email = 'admin@sindicato.es' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/auth/forgot-password' -Body $forgotBody -ContentType 'application/json' | Out-Null

Start-Sleep -Milliseconds 1500
$msgs = Invoke-RestMethod -Method Get -Uri 'http://localhost:8025/api/v2/messages'
$adminMsg = $msgs.items | Where-Object { ($_.Content.Headers.To -join ',') -like '*admin@sindicato.es*' } | Select-Object -First 1
if ($null -eq $adminMsg) {
    throw 'No se encontro correo de recuperacion para admin@sindicato.es'
}

$match = [regex]::Match($adminMsg.Content.Body, 'token=([a-f0-9\-]{36})')
if (-not $match.Success) {
    throw 'No se pudo extraer token para admin'
}
$token = $match.Groups[1].Value

$resetBody = @{ token = $token; newPassword = 'Admin#12345A' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/auth/reset-password' -Body $resetBody -ContentType 'application/json' | Out-Null

$loginBody = @{ email = 'admin@sindicato.es'; password = 'Admin#12345A' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/auth/login' -Body $loginBody -ContentType 'application/json'
if ([string]::IsNullOrWhiteSpace($login.accessToken)) {
    throw 'No se obtuvo accessToken tras reset admin'
}

Write-Output 'ADMIN_RESET_OK'
