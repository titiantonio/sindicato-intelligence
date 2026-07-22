# Fecha

2026-07-22

# Objetivo

Revisar los scripts del repositorio, retirar duplicados u obsoletos y conservar solo los necesarios para desarrollo local, entrega TFM, validacion n8n y configuracion local segura de IA.

# Contexto

Tarea de mantenimiento operativo posterior al Sprint 12. La arquitectura vigente mantiene `WF-01` en n8n y `WF-02` a `WF-06` en Spring Boot, por lo que scripts antiguos de Sprint 11 y arranques previos podian inducir a error.

# Fase MVP

Fase 12 / mantenimiento operativo de infraestructura y configuracion ADMIN.

# Archivos modificados

- Eliminado `dev-startup.ps1`.
- Eliminado `dev-startup.md`.
- Eliminado `scripts/validate-sprint11-acceptance.ps1`.
- Eliminado `scripts/fake-telegram-server.ps1`.
- Eliminado `set_ai_env.ps1` local ignorado.
- Conservado `dev-start.ps1`.
- Conservados `tfm-start.ps1`, `tfm-check.ps1`, `tfm-stop.ps1` y `tfm-reset.ps1`.
- Conservado `n8n/validate-workflows.ps1`.
- Conservado `set_ai_env.example.ps1`.
- Actualizado `backend/pom.xml`.
- Actualizado `CHANGELOG.md`.
- Actualizado `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

# Decisiones

- `dev-startup.ps1` se elimina porque duplicaba `dev-start.ps1`, reseteaba volumenes y mantenia supuestos antiguos de solo 3 migraciones Flyway.
- `scripts/validate-sprint11-acceptance.ps1` se elimina porque dependia de credenciales bootstrap antiguas y de un flujo de aceptacion anterior a Playwright y a la configuracion ADMIN actual.
- `scripts/fake-telegram-server.ps1` se elimina porque solo daba soporte a la aceptacion antigua de Sprint 11 y no forma parte de los comandos vigentes documentados.
- `set_ai_env.ps1` se elimina por ser un script local ignorado con secreto real; se conserva exclusivamente `set_ai_env.example.ps1` como plantilla segura.
- No se modifican `dev-start.ps1`, `tfm-*` ni `n8n/validate-workflows.ps1` porque estan alineados con README, guia TFM y `AGENTS.md`.

# Pruebas o verificaciones

- Inventario final: quedan 7 scripts PowerShell necesarios.
- Sintaxis PowerShell validada mediante parser para `dev-start.ps1`, `tfm-start.ps1`, `tfm-check.ps1`, `tfm-stop.ps1`, `tfm-reset.ps1`, `n8n/validate-workflows.ps1` y `set_ai_env.example.ps1`: OK.
- n8n: `powershell -ExecutionPolicy Bypass -File "n8n\\validate-workflows.ps1"` OK para `WF-01`.
