$ErrorActionPreference = 'Stop'

Write-Host "Parando stack Docker TFM..." -ForegroundColor Cyan
docker compose stop
Write-Host "Stack TFM parado" -ForegroundColor Green
