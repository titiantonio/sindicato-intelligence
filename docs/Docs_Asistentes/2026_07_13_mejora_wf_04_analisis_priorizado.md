# 2026-07-13 - Mejora WF-04 analisis priorizado

## Objetivo

Mejorar `WF-04` para generar analisis IA mas utiles, priorizados y trazables, con automatizacion inmediata para eventos `HIGH` y `CRITICAL` y control de obsolescencia cuando el evento cambia.

## Contexto

- Fase MVP afectada: Fase 12, automatizaciones internas, observabilidad IA y configuracion ADMIN.
- Tarea Documento 31: anadido bloque `19.37 Mejora de analisis WF-04 y priorizacion automatica`.
- `WF-04` se mantiene en Spring Boot; no se reintroduce logica en n8n.

## Archivos modificados

- `backend/src/main/resources/db/migration/V23__enhance_event_ai_analysis.sql`.
- `backend/src/main/java/es/sindicato/intelligence/analysis/**`.
- `backend/src/main/java/es/sindicato/intelligence/automation/**`.
- `backend/src/main/java/es/sindicato/intelligence/event/**`.
- `frontend/src/app/core/models/event.models.ts`.
- `frontend/src/app/features/events/event-detail-page.component.html`.
- `frontend/src/app/features/events/event-detail-page.component.spec.ts`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `CHANGELOG.md`.
- `backend/pom.xml`.

## Decisiones tomadas

- `WF-03` no ejecuta IA de analisis directamente; solo solicita ejecucion inmediata de `WF04_ANALYSIS` para eventos `HIGH` y `CRITICAL`.
- `WF-04` decide candidatos por prioridad, volumen de noticias y recencia.
- `LOW` no se analiza automaticamente salvo que tenga al menos 3 noticias y este estabilizado.
- Los analisis guardan snapshot de `event.updatedAt`; si el evento cambia despues, el analisis queda obsoleto.
- Se persisten `affectedGroups` y `recommendedMonitoring`, que el prompt ya solicitaba.
- Se anaden `analysisType` y `generationTrigger` para observabilidad funcional.

## Verificaciones

- `mvn -q -DskipTests compile` ejecutado en `backend`: OK.
- Backend focal WF-04: `mvn -q "-Dtest=GenerateAnalysisUseCaseTest,GenerateAnalysisPromptBuilderTest,GeminiAnalysisAIProviderTest,DeterministicAnalysisAIProviderTest,EventAIAnalysisTest,JpaEventAIAnalysisRepositoryTest,ProcessPendingEventAnalysisUseCaseTest,DetectEventUseCaseTest" test`: OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/event-detail-page.component.spec.ts --include=src/app/core/services/analysis.service.spec.ts`: OK, 3 tests.

## Observaciones

- `EventControllerTest` aislado mantiene 2 fallos no atribuibles a `WF-04` en la base local compartida: expectativa antigua de `matchDecision=NEW_EVENT` frente a `REVIEW_RECOMMENDED_NEW_EVENT` y orden por ids con datos acumulados.
