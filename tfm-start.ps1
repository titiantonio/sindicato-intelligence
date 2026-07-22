param(
    [switch]$NoBuild,
    [switch]$SkipN8nSetup
)

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

        $key = $trimmed.Substring(0, $separator).Trim()
        $value = $trimmed.Substring($separator + 1).Trim()
        $values[$key] = $value
    }

    return $values
}

function New-BasicAuthHeader {
    param(
        [string]$User,
        [string]$Password
    )

    $bytes = [Text.Encoding]::ASCII.GetBytes("${User}:${Password}")
    return 'Basic ' + [Convert]::ToBase64String($bytes)
}

function Invoke-DockerCommand {
    param(
        [string]$Description,
        [string[]]$Arguments,
        [switch]$IgnoreFailure
    )

    Write-Host $Description -ForegroundColor Cyan
    & docker @Arguments
    if ($LASTEXITCODE -ne 0 -and -not $IgnoreFailure) {
        throw "Fallo Docker: $Description"
    }
}

function Stop-DevelopmentStackIfPresent {
    param([string]$Root)

    $databaseDir = Join-Path $Root 'database'
    $databaseCompose = Join-Path $databaseDir 'docker-compose.yml'
    $databaseEnv = Join-Path $databaseDir '.env'
    $databaseEnvExample = Join-Path $databaseDir '.env.example'

    if (-not (Test-Path -LiteralPath $databaseCompose)) {
        return
    }

    $composeArgs = @('compose', '--project-directory', $databaseDir)
    if (Test-Path -LiteralPath $databaseEnv) {
        $composeArgs += @('--env-file', $databaseEnv)
    }
    elseif (Test-Path -LiteralPath $databaseEnvExample) {
        $composeArgs += @('--env-file', $databaseEnvExample)
    }
    $composeArgs += @('-f', $databaseCompose, 'stop')

    Invoke-DockerCommand `
        -Description 'Parando infraestructura Docker de desarrollo previa si estaba activa...' `
        -Arguments $composeArgs `
        -IgnoreFailure
}

function Stop-LocalProjectProcessOnPort {
    param(
        [int]$Port,
        [string]$Description,
        [string]$ExpectedCommandPattern
    )

    $connections = @(Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
    if ($connections.Count -eq 0) {
        return
    }

    $processIds = @($connections | Select-Object -ExpandProperty OwningProcess -Unique | Where-Object { $_ -gt 0 })
    foreach ($processId in $processIds) {
        $process = Get-CimInstance Win32_Process -Filter "ProcessId = $processId" -ErrorAction SilentlyContinue
        if ($null -eq $process) {
            continue
        }

        $commandLine = if ($process.CommandLine) { $process.CommandLine } else { $process.Name }
        if ($commandLine -match $ExpectedCommandPattern) {
            Write-Host "Parando $Description local en puerto ${Port} (PID $processId)..." -ForegroundColor Yellow
            Stop-Process -Id $processId -Force
            Start-Sleep -Seconds 2
            continue
        }

        throw "El puerto ${Port} esta ocupado por un proceso no reconocido ($($process.Name), PID $processId). Cierra ese proceso antes de arrancar el stack TFM."
    }
}

function Wait-HttpOk {
    param(
        [string]$Url,
        [hashtable]$Headers = @{},
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            Invoke-WebRequest -Uri $Url -Headers $Headers -UseBasicParsing -TimeoutSec 5 | Out-Null
            return
        }
        catch {
            Start-Sleep -Seconds 3
        }
    } while ((Get-Date) -lt $deadline)

    throw "Timeout esperando disponibilidad de $Url"
}

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envPath = Join-Path $root '.env'
$envExamplePath = Join-Path $root '.env.example'

if (-not (Test-Path -LiteralPath $envPath)) {
    Copy-Item -LiteralPath $envExamplePath -Destination $envPath
    Write-Host "Creado .env desde .env.example" -ForegroundColor Green
}

$envValues = Read-EnvFile -Path $envPath

Stop-DevelopmentStackIfPresent -Root $root
Stop-LocalProjectProcessOnPort -Port 8080 -Description 'backend Spring Boot' -ExpectedCommandPattern 'es\.sindicato\.intelligence\.IntelligenceApplication|spring-boot'
Stop-LocalProjectProcessOnPort -Port 4200 -Description 'frontend Angular' -ExpectedCommandPattern 'ng(\.cmd)?\s+serve|angular|@angular'

Write-Host "Levantando stack Docker TFM..." -ForegroundColor Cyan
$composeArgs = @('compose', 'up', '-d')
if (-not $NoBuild) {
    $composeArgs += '--build'
}

& docker @composeArgs
if ($LASTEXITCODE -ne 0) {
    throw 'docker compose up fallo'
}

Write-Host "Esperando backend..." -ForegroundColor Yellow
Wait-HttpOk -Url 'http://localhost:8080/api/v1/health' -TimeoutSeconds 240

Write-Host "Esperando frontend..." -ForegroundColor Yellow
Wait-HttpOk -Url 'http://localhost:4200' -TimeoutSeconds 120

Write-Host "Esperando n8n..." -ForegroundColor Yellow
$n8nHeaders = @{ Authorization = New-BasicAuthHeader -User $envValues['N8N_BASIC_AUTH_USER'] -Password $envValues['N8N_BASIC_AUTH_PASSWORD'] }
Wait-HttpOk -Url 'http://localhost:5678' -Headers $n8nHeaders -TimeoutSeconds 180

if (-not $SkipN8nSetup) {
    Write-Host "Configurando owner de n8n si es necesario..." -ForegroundColor Yellow
    $ownerBody = @{
        email = $envValues['N8N_BASIC_AUTH_USER']
        firstName = 'N8N'
        lastName = 'Service'
        password = $envValues['N8N_BASIC_AUTH_PASSWORD']
    } | ConvertTo-Json -Compress

    try {
        Invoke-RestMethod -Method Post -Uri 'http://localhost:5678/rest/owner/setup' -Headers $n8nHeaders -Body $ownerBody -ContentType 'application/json' -TimeoutSec 15 | Out-Null
        Write-Host "Owner n8n configurado" -ForegroundColor Green
    }
    catch {
        Write-Host "Owner n8n ya configurado o endpoint no requiere accion" -ForegroundColor DarkYellow
    }

    Write-Host "Importando WF-01 si no existe..." -ForegroundColor Yellow
    $workflowList = & docker compose exec -T n8n n8n list:workflow 2>&1
    $workflowListText = ($workflowList | Out-String)
    if ($workflowListText -match 'WF-01-Capture-News') {
        Write-Host "WF-01 ya existe en n8n" -ForegroundColor Green
    }
    else {
        & docker compose exec -T n8n n8n import:workflow --input=/workflows/wf_01_capture_news.json
        if ($LASTEXITCODE -ne 0) {
            throw 'No se pudo importar WF-01 en n8n'
        }
        Write-Host "WF-01 importado en n8n" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Stack TFM listo" -ForegroundColor Green
Write-Host "Frontend: http://localhost:4200"
Write-Host "Backend health: http://localhost:8080/api/v1/health"
Write-Host "Swagger/OpenAPI: http://localhost:8080/swagger-ui/index.html"
Write-Host "n8n: http://localhost:5678"
Write-Host "MailHog: http://localhost:8025"
Write-Host ""
Write-Host "Credenciales demo: consultar el documento de contrasenas entregado al profesorado."
