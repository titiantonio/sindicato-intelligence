# T8.4 persistencia API WF05 contenido

## Fecha

2026-06-08

## Objetivo

Persistir contenido generado, exponer API de generacion/revision y crear el workflow n8n WF-05.

## Contexto

- Fase MVP: Fase 9, Contenido.
- Sprint: Sprint 8.
- Tarea Documento 31: T8.4 Persistir contenido.
- El Documento 20 ya define la tabla `generated_content`; no se requiere nueva migracion Flyway.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/application/ApproveContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/RejectContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/GeneratedContentEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/JpaGeneratedContentRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/content/api/GenerateContentRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/content/api/GeneratedContentResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/content/api/ContentController.java`
- `backend/src/test/java/es/sindicato/intelligence/content/infrastructure/JpaGeneratedContentRepositoryTest.java`
- `backend/src/test/java/es/sindicato/intelligence/content/api/ContentControllerTest.java`
- `n8n/workflows/wf_05_generate_content.json`

## Decisiones tomadas

- Usar el esquema existente `generated_content` sin modificar migraciones.
- Exponer `POST /api/v1/content/generate` para WF-05 y backoffice futuro.
- Exponer `POST /api/v1/content/{id}/approve` y `POST /api/v1/content/{id}/reject` porque ya forman parte del contrato API oficial.
- Mantener `createdBy` resuelto internamente por `CurrentContentAuthorProvider`; no se expone en el body.
- Crear WF-05 con `Webhook Trigger` y `Manual Trigger`, normalizando `eventId`, `analysisId`, `channel`, `tone` y `length` antes de delegar en Spring Boot.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=GeneratedContentTest,GenerateContentUseCaseTest,GenerateContentPromptBuilderTest,DeterministicContentAIProviderTest,GeminiContentAIProviderTest,JpaGeneratedContentRepositoryTest,ContentControllerTest" test` desde `backend` el 2026-06-09.
- Resultado: `Tests run: 17, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
- Validado `n8n/workflows/wf_05_generate_content.json` con `ConvertFrom-Json`.
- Resultado: JSON valido.
