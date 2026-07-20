# Dockerizacion de entrega TFM

Fecha: 2026-07-20

## Objetivo

Preparar una ejecucion Docker reproducible para que el profesorado pueda corregir el proyecto desde una descarga limpia del repositorio publico.

## Contexto

- Fase Documento 30: Fase 0 infraestructura base, Fase 1 backend base, Fase 2 modelo de datos y entrega operativa del MVP.
- Documento 31 actualizado en el bloque de infraestructura/devops.
- Se mantienen credenciales demo locales para evaluacion, con documento de contrasenas separado para el profesorado.

## Archivos modificados

- `docker-compose.yml`.
- `.env.example`.
- `backend/Dockerfile`.
- `backend/.dockerignore`.
- `frontend/.dockerignore`.
- `n8n/workflows/wf_01_capture_news.json`.
- `database/docker-compose.yml`.
- `database/.env.example`.
- `tfm-start.ps1`.
- `tfm-stop.ps1`.
- `tfm-reset.ps1`.
- `tfm-check.ps1`.
- `README.md`.
- `docs/guia_ejecucion_tfm.md`.
- `backend/pom.xml`.
- `backend/src/main/resources/application.yml`.
- `backend/src/main/resources/application-prod.yml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones

- Crear un compose raiz de entrega con `postgres`, `backend`, `frontend`, `n8n` y `mailhog`.
- Mantener `database/docker-compose.yml` como compose auxiliar historico de infraestructura, pero orientar la entrega TFM al compose raiz.
- Usar `.env.example` publico para generar `.env` local automaticamente desde `tfm-start.ps1`.
- Mantener proveedor IA `deterministic` por defecto para que la correccion no dependa de claves externas.
- Dejar Telegram deshabilitado por defecto para evitar depender de credenciales externas.
- Parametrizar `WF-01` con `BACKEND_BASE_URL`; en Docker completo apunta a `http://backend:8080`.
- Automatizar la importacion de `WF-01` desde `tfm-start.ps1` si no existe en n8n, dejandolo inactivo por seguridad en la correccion.

## Pruebas y verificaciones

- `docker compose --env-file .env.example config`: correcto.
- Parser PowerShell para `tfm-start.ps1`, `tfm-stop.ps1`, `tfm-reset.ps1` y `tfm-check.ps1`: correcto.
- `n8n/validate-workflows.ps1`: correcto.
- `docker compose --env-file .env.example build backend frontend`: correcto.
- `./tfm-start.ps1 -NoBuild`: correcto tras corregir permisos de logs del backend y la importacion CLI de n8n.
- `./tfm-check.ps1`: correcto, validando PostgreSQL, backend health, frontend, n8n, MailHog y WF-01 importado.
- `./tfm-start.ps1 -NoBuild` repetido: correcto, validando idempotencia y no duplicacion de WF-01.
