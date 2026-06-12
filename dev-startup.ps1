# ============================================================================
# Script de arranque local para desarrollo Sindicato Intelligence
# ============================================================================
# Objetivo: resetear base de datos, levantar stack Docker y backend en secuencia
# Uso: .\dev-startup.ps1
# ============================================================================

$ErrorActionPreference = 'Stop'

Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║ Sindicato Intelligence - Dev Startup                          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

# ============================================================================
# 1. Determinar rutas
# ============================================================================
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = $scriptDir
$databaseDir = Join-Path $projectRoot "database"
$backendDir = Join-Path $projectRoot "backend"

Write-Host "`n[1/5] Verificando directorios..." -ForegroundColor Yellow
if (-not (Test-Path $databaseDir)) { throw "No se encontró: $databaseDir" }
if (-not (Test-Path $backendDir)) { throw "No se encontró: $backendDir" }
Write-Host "✓ Directorios OK" -ForegroundColor Green

# ============================================================================
# 2. Reset de Docker (down -v + up -d)
# ============================================================================
Write-Host "`n[2/5] Reseteando stack Docker..." -ForegroundColor Yellow
Push-Location $databaseDir
try {
    Write-Host "  → docker compose down -v" -ForegroundColor Gray
    & docker compose down -v 2>&1 | Out-Null
    
    Write-Host "  → docker compose up -d" -ForegroundColor Gray
    & docker compose up -d 2>&1 | Out-Null
    
    Write-Host "✓ Stack Docker levantado" -ForegroundColor Green
}
finally {
    Pop-Location
}

# ============================================================================
# 3. Esperar a que PostgreSQL esté listo (puerto 5432)
# ============================================================================
Write-Host "`n[3/5] Esperando disponibilidad de PostgreSQL..." -ForegroundColor Yellow
$maxRetries = 60
$retries = 0
$postgresReady = $false

while ($retries -lt $maxRetries) {
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("localhost", 5432)
        $connection.Close()
        $postgresReady = $true
        break
    }
    catch {
        $retries++
        Start-Sleep -Milliseconds 1000
        Write-Host -NoNewline "." -ForegroundColor Gray
    }
}

if (-not $postgresReady) {
    throw "PostgreSQL no está disponible tras $maxRetries segundos"
}
Write-Host " ✓" -ForegroundColor Green
Write-Host "✓ PostgreSQL accesible en localhost:5432" -ForegroundColor Green

# ============================================================================
# 4. Validar Flyway (queryear schema_history)
# ============================================================================
Write-Host "`n[4/5] Validando migraciones Flyway..." -ForegroundColor Yellow

$maxRetries = 30
$retries = 0
$flywayOk = $false

while ($retries -lt $maxRetries) {
    try {
        $query = "SELECT COUNT(*) as cnt FROM flyway_schema_history WHERE version IS NOT NULL;"
        $result = & docker exec sindicato-postgres psql -U sindicato -d sindicato_intelligence -t -A -c $query 2>&1
        
        if ($result -match '^\d+$') {
            $count = [int]$result.Trim()
            if ($count -eq 3) {
                $flywayOk = $true
                Write-Host "✓ Flyway: V1, V2, V3 aplicadas ($count migraciones)" -ForegroundColor Green
                break
            }
            elseif ($count -gt 0) {
                Write-Host "⚠ Advertencia: Se encontraron $count migraciones (se esperaban 3)" -ForegroundColor Yellow
                $flywayOk = $true
                break
            }
        }
    }
    catch {
        # silenciar errores mientras se conecta
    }
    
    $retries++
    Start-Sleep -Milliseconds 500
    Write-Host -NoNewline "." -ForegroundColor Gray
}

if (-not $flywayOk) {
    Write-Host "`n⚠ No se pudo validar Flyway, pero continuando..." -ForegroundColor Yellow
}

# ============================================================================
# 5. Levantar backend
# ============================================================================
Write-Host "`n[5/5] Levantando backend (Spring Boot)..." -ForegroundColor Yellow
Write-Host "  → Ubicación: $backendDir" -ForegroundColor Gray
Write-Host "  → URL: http://localhost:8080" -ForegroundColor Gray
Write-Host "  → Health: http://localhost:8080/api/v1/health" -ForegroundColor Gray

Push-Location $backendDir
try {
    Write-Host "`n" -ForegroundColor Gray
    Write-Host "Iniciando aplicación..." -ForegroundColor Cyan
    Write-Host "  (Presiona Ctrl+C para detener)" -ForegroundColor Gray
    Write-Host "" -ForegroundColor Gray
    
    & .\mvnw.cmd spring-boot:run
}
catch {
    Write-Host "`n✗ Error al arrancar backend: $_" -ForegroundColor Red
    exit 1
}
finally {
    Pop-Location
}

Write-Host "`n✓ Startup completado" -ForegroundColor Green
