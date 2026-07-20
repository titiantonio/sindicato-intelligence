$ErrorActionPreference = 'Stop'

function Invoke-DockerCompose {
    param(
        [string]$Description,
        [string[]]$Arguments,
        [switch]$IgnoreFailure
    )

    Write-Host $Description -ForegroundColor Cyan
    & docker @Arguments
    if ($LASTEXITCODE -ne 0 -and -not $IgnoreFailure) {
        throw "Fallo Docker Compose: $Description"
    }
}

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$databaseDir = Join-Path $root 'database'
$databaseEnv = Join-Path $databaseDir '.env'
$databaseEnvExample = Join-Path $databaseDir '.env.example'

if (-not (Test-Path -LiteralPath $databaseDir)) {
    throw "No existe el directorio database: $databaseDir"
}

if (-not (Test-Path -LiteralPath $databaseEnv)) {
    if (-not (Test-Path -LiteralPath $databaseEnvExample)) {
        throw "No existe la plantilla de entorno: $databaseEnvExample"
    }

    Copy-Item -LiteralPath $databaseEnvExample -Destination $databaseEnv
    Write-Host "Creado database/.env desde database/.env.example" -ForegroundColor Green
}

Invoke-DockerCompose `
    -Description 'Parando stack Docker TFM si estaba activo...' `
    -Arguments @('compose', '--project-directory', $root, '-f', (Join-Path $root 'docker-compose.yml'), 'stop') `
    -IgnoreFailure

Invoke-DockerCompose `
    -Description 'Parando infraestructura Docker de desarrollo previa si estaba activa...' `
    -Arguments @('compose', '--project-directory', $databaseDir, '--env-file', $databaseEnv, '-f', (Join-Path $databaseDir 'docker-compose.yml'), 'stop') `
    -IgnoreFailure

Invoke-DockerCompose `
    -Description 'Levantando infraestructura Docker de desarrollo...' `
    -Arguments @('compose', '--project-directory', $databaseDir, '--env-file', $databaseEnv, '-f', (Join-Path $databaseDir 'docker-compose.yml'), 'up', '-d')

Write-Host ""
Write-Host "Entorno de desarrollo preparado" -ForegroundColor Green
Write-Host "PostgreSQL: localhost:5432"
Write-Host "n8n: http://localhost:5678"
Write-Host "MailHog: http://localhost:8025"
Write-Host ""
Write-Host "Arranca el backend local en otra terminal:" -ForegroundColor Yellow
Write-Host "  cd backend"
Write-Host "  .\mvnw.cmd spring-boot:run"
Write-Host ""
Write-Host "Arranca el frontend local en otra terminal:" -ForegroundColor Yellow
Write-Host "  cd frontend"
Write-Host "  npm.cmd start"
Write-Host ""
Write-Host "Frontend dev: http://localhost:4200"
Write-Host "Backend dev: http://localhost:8080/api/v1/health"
