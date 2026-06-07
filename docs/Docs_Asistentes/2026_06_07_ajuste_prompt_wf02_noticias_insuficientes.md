# Ajuste prompt WF-02 noticias insuficientes

## Fecha

2026-06-07

## Objetivo

Corregir el fallo observado al ejecutar `WF-02-Classify-News` cuando Gemini/Gemma recibe una noticia sin resumen ni contenido y responde fuera del JSON requerido.

## Contexto

- Fase MVP: Fase 6, clasificacion IA.
- Documento 30: integracion de clasificacion IA sobre noticias capturadas.
- Documento 31: T5.5, prompt WF-02.
- Error observado en n8n: `500` inicialmente y despues `502` en llamada manual al backend actual.
- Causa confirmada: `Gemini response does not contain a JSON object` para `newsId=829`.
- La noticia `829` tenia titulo generico y `summary`/`content` vacios.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilderTest.java`
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- Mantener la logica de clasificacion en Spring Boot y el proveedor IA externo como dependencia tecnica.
- No aplicar fallback silencioso al proveedor determinista.
- Reforzar el prompt oficial WF-02 para que, ante informacion insuficiente, la IA devuelva JSON valido con `category` `OTROS`, `relevance` `0`, `impact` `LOW`, `urgency` `LOW` y explicacion en `summary`.

## Verificaciones

- `mvn "-Dtest=ClassifyNewsPromptBuilderTest,GeminiAIProviderTest" test`: 6 tests, 0 fallos, 0 errores.
- `mvn test`: 100 tests, 0 fallos, 0 errores.

## Nota operativa

Para que n8n use el prompt corregido, es necesario reiniciar el backend Spring Boot que este escuchando en `localhost:8080` antes de volver a ejecutar `WF-02-Classify-News`.
