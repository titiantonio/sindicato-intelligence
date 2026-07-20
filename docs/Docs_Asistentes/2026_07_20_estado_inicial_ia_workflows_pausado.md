# Estado inicial pausado de IA y workflows

Fecha: 2026-07-20

## Objetivo

Asegurar que, al iniciar el proyecto por primera vez en desarrollo o entrega Docker, las automatizaciones internas y todos los proveedores IA queden pausados hasta activacion expresa por un usuario ADMIN.

## Contexto

- Fase Documento 30: Fase 12 automatizaciones internas, observabilidad IA y configuracion ADMIN.
- Documento 31 actualizado como mantenimiento correctivo `19.42`.
- La configuracion efectiva se controla en PostgreSQL mediante `automation_workflow_settings` y `ai_provider_settings`.

## Archivos modificados

- `backend/src/main/resources/db/migration/V2__seed_initial_data.sql`.
- `backend/pom.xml`.
- `backend/src/main/resources/application.yml`.
- `backend/src/main/resources/application-prod.yml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_07_20_estado_inicial_ia_workflows_pausado.md`.

## Decisiones

- Unificar Flyway en las migraciones consolidadas existentes, dejando el estado pausado directamente en `V2__seed_initial_data.sql` por peticion expresa del usuario.
- Pausar explicitamente `WF02_CLASSIFICATION`, `WF03_EVENT_DETECTION` y `WF04_ANALYSIS`.
- Pausar explicitamente `deterministic` y `gemini`.
- No modificar `ai_workflow_settings`: se mantiene la asignacion tecnica de modelo/proveedor, pero no se ejecuta mientras el proveedor este desactivado.

## Pruebas y verificaciones

- Reset de BBDD de desarrollo eliminando el volumen Docker `database_postgres_data`.
- Arranque de backend Spring Boot contra BBDD limpia para aplicar Flyway desde cero.
- Verificado `flyway_schema_history`: solo `V1__create_mvp_schema.sql` y `V2__seed_initial_data.sql`, ambas con `success=true`.
- Verificado `automation_workflow_settings`: `WF02_CLASSIFICATION`, `WF03_EVENT_DETECTION` y `WF04_ANALYSIS` con `enabled=false` y `running=false`.
- Verificado `ai_provider_settings`: `deterministic` y `gemini` con `enabled=false`.
