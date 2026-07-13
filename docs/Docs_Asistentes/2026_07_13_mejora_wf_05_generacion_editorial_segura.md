# 2026-07-13 - Mejora WF-05 generacion editorial segura

## Objetivo

Mejorar `WF-05-Generate-Content` tras las mejoras de `WF-03` y `WF-04`, haciendo la generacion de contenido mas segura, trazable y coherente con el estado vigente del evento.

## Contexto

- Fase MVP afectada: Fase 12, automatizaciones internas, observabilidad IA y backoffice operativo.
- Tarea Documento 31: `19.38 Mejora de generacion editorial WF-05`.
- `WF-05` se mantiene en Spring Boot y no se reintroduce logica en n8n.
- `Event` sigue siendo la entidad central; el contenido se genera desde evento y analisis, no desde noticias aisladas.

## Archivos modificados

- `backend/src/main/resources/db/migration/V24__enhance_generated_content_trace.sql`.
- `backend/src/main/java/es/sindicato/intelligence/content/**`.
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventNewsAssociationTrace*.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/JpaEventRepository.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/api/EventController.java`.
- `backend/src/main/java/es/sindicato/intelligence/publication/api/PublicationController.java`.
- `backend/src/test/java/es/sindicato/intelligence/content/**`.
- `frontend/src/app/core/models/content.models.ts`.
- `frontend/src/app/core/models/event.models.ts`.
- `frontend/src/app/core/services/content.service.ts`.
- `frontend/src/app/features/events/event-detail-page.component.*`.
- `frontend/src/app/features/content/content-page.component.spec.ts`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `CHANGELOG.md`.
- `backend/pom.xml`.
- `frontend/package.json`.
- `frontend/package-lock.json`.

## Decisiones

- Se bloquea `WF-05` si el evento no esta activo, esta descartado manualmente o el analisis seleccionado esta obsoleto respecto a `event.updatedAt`.
- Si no se indica `analysisId`, se selecciona el analisis vigente mas reciente.
- Se evita crear contenido duplicado cuando ya existe contenido `PENDING_REVIEW` o `APPROVED` para el mismo evento, analisis, canal y tipo editorial.
- Se anaden tipos editoriales `TELEGRAM_POST`, `TELEGRAM_SHORT` y `UNION_STATEMENT`, manteniendo compatibilidad con la longitud existente.
- Se persisten `content_type`, `length` y `generation_metadata` para trazabilidad editorial.
- El prompt `WF-05` incorpora campos enriquecidos de `WF-04` y trazabilidad resumida de asociaciones de `WF-03`.
- Gemini conserva el intento normal y reintenta una vez con contexto reducido si la respuesta viene vacia o sin JSON valido.
- La respuesta IA se valida antes de persistir: titulo, mensaje, hashtags, URLs permitidas y longitud maxima.

## Pruebas o verificaciones

- Backend compile: `mvnw.cmd -q -DskipTests compile` OK.
- Backend focal WF-05: `mvnw.cmd -q "-Dtest=GenerateContentUseCaseTest,GenerateContentPromptBuilderTest,GeminiContentAIProviderTest,DeterministicContentAIProviderTest,JpaGeneratedContentRepositoryTest,ContentControllerTest" test` OK.
- Backend unitario adicional: `mvnw.cmd -q "-Dtest=GenerateContentUseCaseTest" test` OK.
- Frontend focal WF-05: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/event-detail-page.component.spec.ts --include=src/app/core/services/content.service.spec.ts --include=src/app/features/content/content-page.component.spec.ts` OK, 11 tests.
- Frontend build: `npm.cmd run build` OK.
