# 2026-06-16 - Configuracion ADMIN de automatizaciones migradas

## Objetivo

Implementar configuracion operativa administrable para `WF-02`, `WF-03` y `WF-04` migrados a Spring Boot, y completar la ejecucion visible de `WF-05` y `WF-06` desde el frontend.

## Contexto

- Fase MVP afectada: consolidacion posterior a Fases 6-11 del Documento 30.
- Sprint afectado: Sprint 12, tareas `T12.11` a `T12.17` del Documento 31.
- `WF-01-Capture-News` permanece en n8n.
- `WF-02` a `WF-06` permanecen fuera de n8n y se ejecutan por Spring Boot/Angular.

## Archivos modificados

- Backend:
  - `backend/src/main/resources/db/migration/V7__automation_workflow_settings.sql`
  - `backend/src/main/java/es/sindicato/intelligence/automation/**`
  - `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
  - `backend/src/test/java/es/sindicato/intelligence/automation/**`
  - `backend/pom.xml`
- Frontend:
  - `frontend/src/app/core/models/automation.models.ts`
  - `frontend/src/app/core/services/automation.service.ts`
  - `frontend/src/app/core/services/content.service.ts`
  - `frontend/src/app/features/automation-settings/**`
  - `frontend/src/app/features/events/event-detail-page.component.*`
  - `frontend/src/app/features/content/content-page.component.*`
  - `frontend/src/app/layout/shell/shell.component.*`
  - `frontend/src/app/app.routes.ts`
- Documentacion:
  - `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`
  - `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
  - `CHANGELOG.md`

## Decisiones tomadas

- La configuracion dinamica se persiste en PostgreSQL mediante `automation_workflow_settings`.
- El scheduler unico `AutomationWorkflowScheduler` sustituye los processors especificos por workflow.
- `WF02_CLASSIFICATION` se inicializa con `batchSize=1` para no saturar proveedores IA gratuitos.
- La configuracion queda restringida a `ADMIN`; la ejecucion manual sigue disponible para `ADMIN` y `EDITOR`.
- `WF-05` se ejecuta bajo demanda desde detalle de evento.
- `WF-06` se ejecuta desde contenido aprobado con `Publicar ahora` o con programacion existente.

## Pruebas y verificaciones

- Backend focal: `mvn -q "-Dtest=AutomationControllerTest,RunAutomationWorkflowUseCaseTest,ProcessDueAutomationWorkflowsUseCaseTest" test` OK.
- Backend completo: `mvn test` OK, 223 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 104 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto.
- n8n: `n8n/validate-workflows.ps1` OK.

## Notas

- El build Angular mantiene warnings de presupuesto ya existentes en bundle inicial y SCSS de `sources`/`users`.
- La migracion `V7` se aplico en la base local durante `mvn test`.
