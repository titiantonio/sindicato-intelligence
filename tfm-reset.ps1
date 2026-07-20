param(
    [switch]$Force,
    [switch]$StartAfter
)

$ErrorActionPreference = 'Stop'

if (-not $Force) {
    $confirmation = Read-Host 'Esto eliminara contenedores y volumenes Docker del TFM. Escribe RESET para continuar'
    if ($confirmation -ne 'RESET') {
        Write-Host 'Reset cancelado' -ForegroundColor Yellow
        exit 0
    }
}

Write-Host "Eliminando stack y volumenes TFM..." -ForegroundColor Cyan
docker compose down -v --remove-orphans
Write-Host "Reset completado" -ForegroundColor Green

if ($StartAfter) {
    & (Join-Path $PSScriptRoot 'tfm-start.ps1')
}
