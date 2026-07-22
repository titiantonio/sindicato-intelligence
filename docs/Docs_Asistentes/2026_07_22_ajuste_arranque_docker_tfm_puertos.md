# Ajuste Arranque Docker TFM Puertos

## Objetivo

Evitar que el arranque Docker TFM falle cuando la infraestructura Docker de desarrollo ya esta activa y ocupa puertos compartidos.

## Contexto

Durante la validacion del flujo `tfm-reset.ps1 -Force -StartAfter`, las imagenes Docker de backend y frontend se construyeron correctamente, pero `docker compose up` fallo al publicar PostgreSQL porque el contenedor de desarrollo `sindicato-postgres` ya usaba `5432`.

## Archivos modificados

- `tfm-start.ps1`.
- `docs/guia_ejecucion_tfm.md`.
- `CHANGELOG.md`.

## Decisiones tomadas

- `tfm-start.ps1` detiene preventivamente el stack Docker de desarrollo definido en `database/docker-compose.yml` antes de levantar el stack TFM.
- La parada usa `database/.env` si existe o `database/.env.example` como respaldo para permitir resolver variables del compose.
- Se usa `stop`, no `down -v`, para liberar puertos sin borrar volumenes de desarrollo.
- `tfm-start.ps1` detiene tambien procesos locales reconocidos del propio proyecto en `8080` y `4200`.
- Si un puerto lo ocupa un proceso no reconocido, el script no lo mata y falla con un mensaje explicito.

## Pruebas y verificaciones

Comandos ejecutados:

```powershell
.\tfm-reset.ps1 -Force -StartAfter
.\tfm-check.ps1
```

Resultados:

- Primer intento de `tfm-reset.ps1 -Force -StartAfter`: fallo por puerto `5432` ocupado por `sindicato-postgres` del entorno de desarrollo.
- Segundo intento tras parar Docker de desarrollo: fallo por puerto `8080` ocupado por backend Spring Boot local del proyecto.
- Tras ampliar `tfm-start.ps1`, el tercer intento detuvo Docker de desarrollo y procesos locales reconocidos en `8080` y `4200`, construyo/levanto el stack TFM, configuro owner n8n e importo `WF-01-Capture-News` correctamente.
- `tfm-check.ps1`: OK PostgreSQL, backend health, frontend, n8n, MailHog y `WF-01` importado.
