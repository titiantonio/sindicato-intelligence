# Trazabilidad logs WF-02 clasificacion

## Fecha

2026-06-07

## Objetivo

Permitir comprobar desde el backend si `WF-02-Classify-News` esta enviando noticias a clasificar, si el caso de uso termina correctamente o si falla por respuesta no valida del proveedor IA.

## Contexto

- Fase MVP: Fase 6, clasificacion IA.
- Documento 30: clasificacion IA de noticias capturadas.
- Documento 31: T5.6 workflow n8n, T5.7 persistir clasificacion.
- Situacion observada: n8n mostraba ejecucion OK, pero la base de datos seguia con una sola clasificacion.
- Reproduccion manual: `POST /api/v1/classifications/classify` con `newsId=34` devolvio `502` y `Gemini response does not contain a JSON object`.

## Diagnostico

- `WF-02` podia terminar OK aunque alguna llamada HTTP fallase porque el nodo `Classify News` tiene `onError: continueRegularOutput`.
- `GET /api/v1/news` devuelve un array JSON. Se hizo robusto el filtro del workflow para soportar tanto array en un unico item de n8n como items individuales.
- El proveedor IA puede responder texto no JSON; ahora el backend deja un log `WARN` con un fragmento acotado de la respuesta para diagnostico.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`
- `n8n/workflows/wf_02_classify_news.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- Añadir logs `INFO` al inicio y exito de cada clasificacion.
- Añadir log `WARN` si se intenta clasificar una noticia ya clasificada.
- Añadir log `ERROR` si falla la clasificacion de una noticia.
- Añadir log `WARN` en `GeminiAIProvider` cuando la respuesta no contiene objeto JSON.
- No registrar API keys ni prompts completos.
- Limitar el fragmento de respuesta Gemini logueado a 500 caracteres.

## Verificaciones

- Validado JSON de `n8n/workflows/wf_02_classify_news.json` con `ConvertFrom-Json`.
- `mvn "-Dtest=ClassifyNewsUseCaseTest,GeminiAIProviderTest" test`: 7 tests, 0 fallos, 0 errores.
- `mvn test`: 100 tests, 0 fallos, 0 errores.

## Uso operativo

Al ejecutar `WF-02`, revisar la consola donde esta arrancado `mvn spring-boot:run`.

Logs esperados:

```text
classification started: newsId=34, title='...'
classification completed: newsId=34, classificationId=..., category=..., subcategory='...', relevance=..., impact=..., urgency=...
```

Si falla Gemini:

```text
Gemini response does not contain JSON object. responseSnippet='...'
classification failed: newsId=34, reason=Gemini response does not contain a JSON object
```

## Nota operativa

Reiniciar el backend e importar/actualizar el workflow en n8n antes de repetir la prueba.
