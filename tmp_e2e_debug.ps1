$ErrorActionPreference = 'Continue'

Write-Output 'STEP1_LOGIN_ADMIN'
try {
    $login = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/auth/login' -Body (@{ email = 'admin@sindicato.es'; password = 'Admin#12345A' } | ConvertTo-Json) -ContentType 'application/json'
    Write-Output ('LOGIN_OK token_len=' + $login.accessToken.Length)
    $token = $login.accessToken
} catch {
    Write-Output ('LOGIN_ERR ' + $_.Exception.Message)
    exit 1
}

$headers = @{ Authorization = "Bearer $token" }
$email = 'e2e.debug.' + [DateTimeOffset]::UtcNow.ToUnixTimeSeconds() + '@sindicato.es'
Write-Output ('STEP2_CREATE_USER ' + $email)
try {
    $created = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/users' -Headers $headers -Body (@{ email = $email; name = 'Debug User'; role = 'EDITOR'; password = 'Initial#123A' } | ConvertTo-Json) -ContentType 'application/json'
    Write-Output ('CREATE_OK id=' + $created.id)
} catch {
    Write-Output ('CREATE_ERR ' + $_.Exception.Message)
}

Write-Output 'STEP3_FORGOT_PASSWORD'
try {
    $forgot = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/auth/forgot-password' -Body (@{ email = $email } | ConvertTo-Json) -ContentType 'application/json'
    Write-Output ('FORGOT_OK ' + $forgot.message)
} catch {
    Write-Output ('FORGOT_ERR ' + $_.Exception.Message)
}
