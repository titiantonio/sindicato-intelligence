# 2026-06-18 - Observabilidad IA diaria en settings

## Fecha

2026-06-18

## Objetivo

Refinar la observabilidad IA de `/settings` para trabajar con metricas diarias, cards estilo dashboard, tabla compacta sin scroll horizontal, modales de error/detalle y registro correcto del modelo IA usado.

## Contexto

Sprint 12 estaba cerrado como bloque de optimizacion y observabilidad ADMIN. La tarea se registra como `T12.26` en el Documento 31, manteniendo `WF-01` en n8n y las automatizaciones backend migradas.

## Fase MVP

Fase 11 - Frontend Angular y cierre Sprint 12 de optimizacion/observabilidad.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/ai/**`
- `backend/src/main/java/es/sindicato/intelligence/classification/**`
- `backend/src/main/java/es/sindicato/intelligence/event/**`
- `backend/src/main/java/es/sindicato/intelligence/analysis/**`
- `backend/src/main/java/es/sindicato/intelligence/content/**`
- `frontend/src/app/features/settings/**`
- `frontend/src/app/core/services/ai-observability.service.ts`
- `frontend/src/app/core/models/ai-observability.models.ts`
- `CHANGELOG.md`
- `backend/pom.xml`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- `GET /api/v1/ai/metrics?date=YYYY-MM-DD` devuelve el dia operativo en zona `Europe/Madrid`.
- La comparativa principal es contra el dia anterior.
- El modo antiguo por `limit` se mantiene si no se envia `date`.
- La tabla diaria pagina en frontend y no muestra `ID` ni `Entidad ID`.
- Los detalles se abren en modal; solo se navega a `/events/{id}` cuando la entidad relacionada es `EVENT`.
- Los proveedores IA exponen `modelName()` para registrar modelo en exitos y fallos sin acoplar casos de uso a infraestructura.

## Pruebas o verificaciones

- `mvn "-Dtest=ListAiMetricsUseCaseTest,AiObservabilityControllerTest,JpaAiObservabilityRepositoryTest,ClassifyNewsUseCaseTest,GenerateContentUseCaseTest" test` OK, 17 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 13 tests.
- `mvn test` OK, 238 tests y Flyway valida 9 migraciones.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 117 tests.
- `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.
