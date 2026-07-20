# Unificacion de contrasenas bootstrap

Fecha: 2026-07-20

## Objetivo

Unificar la password local/bootstrap de PostgreSQL, cuentas default del backend y n8n, anadir acceso basico a la UI de n8n y crear un usuario default `EDITOR`.

## Contexto

- Fase MVP relacionada: seguridad, usuarios e infraestructura local.
- Documento 31 actualizado: pendiente 21 marcado como completado.
- No se modifica el usuario de pruebas `antoniotiti@hotmail.com`.
- La password en claro se documenta solo en `docs/Docs_Asistentes/2026_07_20_contrasenas_proyecto_local.md`, archivo ignorado por Git.

## Archivos modificados

- `.gitignore`.
- `backend/pom.xml`.
- `backend/src/main/resources/application.yml`.
- `backend/src/main/resources/application-prod.yml`.
- `backend/src/main/resources/db/migration/V26__seed_default_editor_and_unify_bootstrap_passwords.sql`.
- `database/docker-compose.yml`.
- `database/.env.example`.
- `database/.env` local ignorado.
- `scripts/validate-sprint11-acceptance.ps1`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones

- Se usa nueva migracion Flyway `V26` para no modificar migraciones ya aplicadas.
- `admin@sindicato.es`, `n8n@sindicato.es` y `editor@sindicato.es` quedan como cuentas default activas con password bootstrap comun.
- La migracion revoca refresh tokens activos de esas cuentas default tras la rotacion.
- n8n UI queda protegida mediante variables `N8N_BASIC_AUTH_*` en Docker Compose local usando `n8n@sindicato.es` como usuario, sin correos personales.
- El usuario interno owner de n8n se actualiza en la SQLite local de n8n a `n8n@sindicato.es` con la password comun.

## Verificacion

- `docker compose config` en `database` OK, con PostgreSQL, n8n backend auth y n8n UI auth resueltos con la password comun.
- `ALTER USER sindicato WITH PASSWORD ...` aplicado en PostgreSQL local activo para alinear el volumen existente.
- `docker compose up -d` recreo `sindicato-postgres` y `sindicato-n8n-dev` con las nuevas variables.
- Flyway Maven `migrate` aplicado contra PostgreSQL local: `V26` registrada con exito.
- Consulta PostgreSQL verificada para `admin@sindicato.es`, `n8n@sindicato.es` y `editor@sindicato.es`; `antoniotiti@hotmail.com` no fue incluido en la migracion.
- `n8n/validate-workflows.ps1` OK para `WF-01`.
- Backend focal: `mvnw.cmd "-Dtest=AuthControllerTest,DatabaseUserDetailsServiceTest,SecurityConfigTest,JwtTokenServiceTest" test` OK, 17 tests, 0 fallos, 0 errores.
- Flyway Maven `validate` OK contra PostgreSQL local.
- Actualizado el usuario interno owner de n8n mediante SQLite local: primero se alineo la password y despues se cambio el email a `n8n@sindicato.es`.
