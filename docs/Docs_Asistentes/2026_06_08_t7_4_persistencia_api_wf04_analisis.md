# T7.4 persistencia API WF04 analisis

## Fecha

2026-06-08

## Objetivo

Persistir analisis IA de eventos, exponer el endpoint de generacion para WF-04 y crear el workflow n8n exportable.

## Contexto

- Fase MVP: Fase 8, Analisis IA.
- Sprint: Sprint 7.
- Tarea Documento 31: T7.4 Persistir analisis.
- El Documento 20 ya define la tabla `event_ai_analysis`; no se requiere nueva migracion Flyway.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/EventAIAnalysisEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/JpaEventAIAnalysisRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/api/GenerateAnalysisRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/api/EventAIAnalysisResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/api/AnalysisController.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/JpaEventAIAnalysisRepositoryTest.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/api/AnalysisControllerTest.java`
- `n8n/workflows/wf_04_generate_analysis.json`
- `CHANGELOG.md`

## Decisiones tomadas

- Usar el esquema existente `event_ai_analysis` sin modificar migraciones.
- Persistir listas como `jsonb` mediante `@JdbcTypeCode(SqlTypes.JSON)`, igual que clasificaciones.
- Exponer `POST /api/v1/analysis/generate` como endpoint interno para WF-04.
- Crear WF-04 con `Webhook Trigger` y `Manual Trigger`; ambos normalizan `eventId` y delegan la generacion en Spring Boot.
- Mantener n8n como orquestador; la generacion y persistencia se ejecutan en backend.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=EventAIAnalysisTest,GenerateAnalysisUseCaseTest,GenerateAnalysisPromptBuilderTest,DeterministicAnalysisAIProviderTest,GeminiAnalysisAIProviderTest,JpaEventAIAnalysisRepositoryTest,AnalysisControllerTest" test` desde `backend`.
- Resultado: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
- Validado `n8n/workflows/wf_04_generate_analysis.json` con `ConvertFrom-Json`: JSON valido.
