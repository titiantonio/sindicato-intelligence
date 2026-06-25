# Detalle operaciones workflows settings

## Fecha

2026-06-25

## Objetivo

Ampliar `/settings > Metricas IA > Operaciones del dia` para mostrar detalle funcional por workflow, incluyendo operaciones IA `WF-02` a `WF-05` y publicaciones Telegram `WF-06`.

## Contexto

La pantalla mostraba metricas tecnicas de IA, pero no explicaba el resultado funcional segun el workflow. Se mantiene `GET /api/v1/ai/metrics` como resumen IA y se anade una vista operativa unificada en automatizaciones.

## Fase MVP

Sprint 12: optimizacion IA, automatizaciones internas, observabilidad y configuracion ADMIN.

## Archivos modificados

- Backend: metricas IA, automatizaciones, publicaciones, migracion Flyway y pruebas focales.
- Frontend: modelos/servicio de automatizaciones y pantalla `/settings`.
- Documentacion: `CHANGELOG.md`, Documento 31 y este registro.

## Decisiones

- `WF-06` se muestra como operacion de workflow, no como metrica IA.
- Los detalles IA se guardan en `ai_operation_metrics.operation_details` como JSONB compacto y sanitizado.
- `WF-02` registra explicitamente `DISCARDED` para `OTROS/FUERA_DE_AMBITO` o `OTROS/INFORMACION_INSUFICIENTE` con relevancia `0`.
- No se almacenan prompts completos, payloads completos, secretos ni respuestas crudas extensas.

## Pruebas o verificaciones

- Backend focal: `mvn "-Dtest=JpaAiObservabilityRepositoryTest,AutomationControllerTest,ListWorkflowOperationsUseCaseTest,ClassifyNewsUseCaseTest,GenerateContentUseCaseTest" test` OK, 23 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/automation.service.spec.ts` OK, 20 tests.
- Backend compile: `mvn -DskipTests compile` OK con version `0.0.72-SNAPSHOT`.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de budgets inicial, `sources`, `audit`, `events` y `users`.
