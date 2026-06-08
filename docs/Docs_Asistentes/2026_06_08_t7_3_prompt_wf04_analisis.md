# T7.3 prompt WF-04 analisis

## Fecha

2026-06-08

## Objetivo

Implementar el prompt oficial WF-04 para analisis de eventos e incorporar proveedores IA compatibles con el caso de uso de analisis.

## Contexto

- Fase MVP: Fase 8, Analisis IA.
- Sprint: Sprint 7.
- Tarea Documento 31: T7.3 Implementar Prompt WF-04.
- El Documento 23 define salida JSON con `executiveSummary`, `unionSummary`, `keyPoints`, `risks`, `opportunities`, `affectedGroups` y `recommendedMonitoring`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisAIProviderException.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisPrompt.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisPromptBuilder.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisAIRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/DeterministicAnalysisAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisPromptBuilderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/DeterministicAnalysisAIProviderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProviderTest.java`
- `CHANGELOG.md`

## Decisiones tomadas

- Crear un prompt builder propio de analisis basado en el Documento 23.
- Mantener reglas explicitas de no inventar informacion y limitar conclusiones a evento/noticias asociadas.
- Crear proveedor determinista para desarrollo y tests cuando `app.ai.provider=deterministic`.
- Crear proveedor Gemini especifico para analisis cuando `app.ai.provider=gemini`, sin reutilizar el contrato de clasificacion.
- Parsear y persistir en el dominio solo los campos contemplados por `event_ai_analysis`; `affectedGroups` y `recommendedMonitoring` quedan disponibles como parte del contrato IA y para futuras ampliaciones.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=EventAIAnalysisTest,GenerateAnalysisUseCaseTest,GenerateAnalysisPromptBuilderTest,DeterministicAnalysisAIProviderTest,GeminiAnalysisAIProviderTest" test` desde `backend`.
- Resultado: `Tests run: 13, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
