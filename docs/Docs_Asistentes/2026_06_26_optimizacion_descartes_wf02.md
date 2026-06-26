# Fecha

2026-06-26

# Objetivo

Evitar que la IA genere datos innecesarios en `WF-02` cuando una noticia se descarta por estar fuera de ambito o por informacion insuficiente.

# Contexto

La intervencion afecta a Fase 6 Clasificacion IA y Sprint 12 Observabilidad IA. Las noticias descartadas no pasan a eventos, por lo que no necesitan `keywords`, `entities` ni `summary`; pedir esos campos aumenta coste de tokens y ruido operativo.

# Fase MVP

- Documento 30: Fase 6 Clasificacion IA.
- Documento 31: registrada la seccion `16.45 Optimizacion de respuesta minima en descartes WF-02 - 2026-06-26`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProviderTest.java`
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Para `FUERA_DE_AMBITO` e `INFORMACION_INSUFICIENTE`, la salida esperada de IA queda reducida a `category`, `subcategory`, `relevance`, `impact` y `urgency`.
- `keywords`, `entities` y `summary` siguen disponibles para noticias clasificables.
- El parser Gemini acepta respuestas minimas y normaliza campos enriquecidos ausentes a listas vacias o texto vacio.
- Las metricas funcionales de descartes conservan estado final y motivo de descarte, pero no registran campos enriquecidos innecesarios.

# Pruebas o verificaciones

- `mvn "-Dtest=ClassifyNewsPromptBuilderTest,ClassifyNewsUseCaseTest,GeminiAIProviderTest,NewsClassificationTest" test`: 17 tests, 0 fallos, 0 errores.
