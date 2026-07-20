# Script de desarrollo local

Fecha: 2026-07-20

## Objetivo

Crear un script de arranque para desarrollo que evite conflictos con el stack Docker TFM y prepare solo la infraestructura necesaria para ejecutar backend y frontend en local.

## Contexto

- Fase Documento 30: Fase 0 infraestructura base y soporte operativo del MVP.
- Documento 31 actualizado en el bloque de infraestructura/devops.
- El stack TFM ejecuta backend y frontend en Docker con build de produccion; no es el flujo recomendado para desarrollo diario.

## Archivos modificados

- `dev-start.ps1`.
- `README.md`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_07_20_script_desarrollo_local.md`.

## Decisiones

- `dev-start.ps1` para primero el stack TFM raiz si estaba activo.
- `dev-start.ps1` para tambien una infraestructura previa de `database/docker-compose.yml` si estaba activa.
- El script no borra volumenes ni datos; solo detiene y arranca contenedores.
- El script levanta solo PostgreSQL, n8n y MailHog desde `database/docker-compose.yml`.
- Backend y frontend se ejecutan manualmente en local para mantener recarga rapida, perfil local y ciclo de desarrollo normal.

## Pruebas y verificaciones

- Sintaxis PowerShell: `[scriptblock]::Create((Get-Content -LiteralPath 'dev-start.ps1' -Raw))` OK.
- Compose desarrollo: `docker compose --project-directory database --env-file database/.env.example -f database/docker-compose.yml config` OK.
- Ejecucion real: `./dev-start.ps1` OK.
- Resultado: stack TFM raiz parado e infraestructura de desarrollo `database/docker-compose.yml` levantada con PostgreSQL, n8n y MailHog.
