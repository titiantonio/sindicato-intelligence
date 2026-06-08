# Logs en casos de uso y reintento Gemini

## Fecha

2026-06-07

## Objetivo

Completar trazabilidad operativa en los casos de uso backend existentes y mejorar el comportamiento ante respuestas intermitentes de Gemini sin contenido textual clasificable.

## Contexto

- Fase MVP: Fase 1 backend base como mantenimiento transversal, y Fase 6 clasificacion IA por el ajuste de `GeminiAIProvider`.
- El usuario observo que `newsId=38` fallo en un intento con `Gemini response does not contain candidates[0].content.parts[0].text` y despues pudo volver a intentarse.
- Tambien se pregunto si los logs ya estaban en todo el proyecto. La respuesta era que existia configuracion global de Logback y logs en clasificacion/Gemini, pero no en todos los casos de uso existentes.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProviderTest.java`
- `backend/src/main/java/es/sindicato/intelligence/source/application/CreateSourceUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/source/application/UpdateSourceUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/source/application/ListSourcesUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/CreateNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/GetNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/ListNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Añadir logs `INFO` de inicio y finalizacion en casos de uso relevantes.
- Añadir logs `WARN` para duplicados, descartes y situaciones recuperables.
- Añadir logs `ERROR` con excepcion completa cuando falla una integracion IA.
- Añadir reintento controlado en Gemini solo para respuestas recuperables sin texto, sin JSON o con JSON invalido.
- No hacer fallback silencioso al proveedor determinista.
- No permitir que la IA acceda directamente a URLs externas desde el prompt; la clasificacion usa el contenido capturado y persistido por el backend.

## Verificaciones

- 2026-06-08: ejecutado `mvn test` desde `backend` con resultado correcto: `Tests run: 101, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS`.

## Nota operativa

El fallo observado en `newsId=38` no significa que la noticia sea inclasificable. Significa que en ese intento Gemini devolvio una respuesta sin texto util en `candidates[0].content.parts[0].text`, algo que puede ocurrir de forma intermitente por finalizacion del modelo, bloqueo de seguridad, limite de salida o respuesta incompleta. El reintento controlado reduce la perdida de noticias por ese tipo de fallo.
