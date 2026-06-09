# T8.3 prompt WF05 contenido

## Fecha

2026-06-08

## Objetivo

Implementar el prompt oficial WF-05 para generacion de contenido Telegram e incorporar proveedores IA compatibles con el caso de uso de contenido.

## Contexto

- Fase MVP: Fase 9, Contenido.
- Sprint: Sprint 8.
- Tarea Documento 31: T8.3 Implementar Prompt WF-05.
- El Documento 23 define salida JSON con `title`, `message` y `hashtags`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/application/ContentAIProviderException.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentPrompt.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentPromptBuilder.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/ContentAIRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/DeterministicContentAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/GeminiContentAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/content/application/GenerateContentPromptBuilderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/content/application/GenerateContentUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/content/infrastructure/DeterministicContentAIProviderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/content/infrastructure/GeminiContentAIProviderTest.java`

## Decisiones tomadas

- Crear un prompt builder propio de contenido basado en el Documento 23.
- Mantener reglas explicitas de no exagerar, no usar lenguaje sensacionalista y no inventar informacion.
- Crear proveedor determinista para desarrollo y tests cuando `app.ai.provider=deterministic`.
- Crear proveedor Gemini especifico para contenido cuando `app.ai.provider=gemini`, sin reutilizar contratos de clasificacion o analisis.
- Forzar salida JSON estructurada con `title`, `message` y `hashtags`.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=GeneratedContentTest,GenerateContentUseCaseTest,GenerateContentPromptBuilderTest,DeterministicContentAIProviderTest,GeminiContentAIProviderTest" test` desde `backend`.
- Resultado: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
