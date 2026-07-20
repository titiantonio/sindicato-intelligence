$ErrorActionPreference = 'Stop'

function Read-EnvFile {
    param([string]$Path)

    $values = @{}
    foreach ($line in Get-Content -LiteralPath $Path) {
        $trimmed = $line.Trim()
        if ($trimmed -eq '' -or $trimmed.StartsWith('#')) {
            continue
        }

        $separator = $trimmed.IndexOf('=')
        if ($separator -lt 1) {
            continue
        }

        $values[$trimmed.Substring(0, $separator).Trim()] = $trimmed.Substring($separator + 1).Trim()
    }

    return $values
}

function New-BasicAuthHeader {
    param([string]$User, [string]$Password)
    $bytes = [Text.Encoding]::ASCII.GetBytes("${User}:${Password}")
    return 'Basic ' + [Convert]::ToBase64String($bytes)
}

function Assert-HttpOk {
    param(
        [string]$Name,
        [string]$Url,
        [hashtable]$Headers = @{}
    )

    try {
        Invoke-WebRequest -Uri $Url -Headers $Headers -UseBasicParsing -TimeoutSec 10 | Out-Null
        Write-Host "OK $Name" -ForegroundColor Green
    }
    catch {
        throw "Fallo $Name en ${Url}: $($_.Exception.Message)"
    }
}

$envPath = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path -LiteralPath $envPath)) {
    throw 'No existe .env. Ejecuta primero .\tfm-start.ps1'
}

$envValues = Read-EnvFile -Path $envPath
$n8nHeaders = @{ Authorization = New-BasicAuthHeader -User $envValues['N8N_BASIC_AUTH_USER'] -Password $envValues['N8N_BASIC_AUTH_PASSWORD'] }

Write-Host "Comprobando stack Docker TFM..." -ForegroundColor Cyan

docker compose ps

docker compose exec -T postgres pg_isready -U $envValues['POSTGRES_USER'] -d sindicato_intelligence | Out-Null
Write-Host "OK PostgreSQL" -ForegroundColor Green

Assert-HttpOk -Name 'Backend health' -Url 'http://localhost:8080/api/v1/health'
Assert-HttpOk -Name 'Frontend' -Url 'http://localhost:4200'
Assert-HttpOk -Name 'n8n' -Url 'http://localhost:5678' -Headers $n8nHeaders
Assert-HttpOk -Name 'MailHog' -Url 'http://localhost:8025/api/v2/messages'

$workflowList = & docker compose exec -T n8n n8n list:workflow 2>&1
if (($workflowList | Out-String) -notmatch 'WF-01-Capture-News') {
    throw 'WF-01-Capture-News no esta importado en n8n'
}
Write-Host "OK WF-01 importado en n8n" -ForegroundColor Green

Write-Host "Comprobacion TFM completada" -ForegroundColor Green
