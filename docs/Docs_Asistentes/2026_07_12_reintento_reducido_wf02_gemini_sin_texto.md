# Reintento reducido WF-02 para Gemini sin texto

## Fecha

2026-07-12

## Objetivo

Diagnosticar la reaparicion del error `Gemini response does not contain candidates[0].content.parts[0].text` y corregir el caso en el que afecta a noticias educativas reales que no deben descartarse como fuera de ambito.

## Contexto

- Fase MVP relacionada: Fase 6 Clasificacion IA.
- Workflow relacionado: `WF-02-Classify-News` en Spring Boot.
- Tarea Documento 31: `19.34 Reintento reducido WF-02 para Gemini sin texto en noticias educativas`.
- Documento revisado por peticion del usuario: `docs/Docs_Asistentes/2026_07_11_fallback_wf02_descarte_manual_noticias.md`.

## Diagnostico

- La correccion anterior funcionaba para noticias fuera de ambito sin senales educativas o sindicales.
- El fallo actual se concentra en `newsId=4611`, una noticia de `educaciontrespuntocero.com` sobre educacion en diversidad LGTBI+ y acoso escolar.
- La noticia contiene senales educativas claras, por lo que el fallback automatico `OTROS/FUERA_DE_AMBITO` se desactiva correctamente.
- Gemini devuelve respuesta sin texto de forma repetida, probablemente por contenido largo o sensible del cuerpo de la noticia.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- Para noticias con senales educativas que reciben respuesta Gemini sin texto, se reintenta una vez con contexto reducido a titulo, URL y resumen capturado por `WF-01`.
- El contenido completo no se reenvia en ese reintento, para reducir bloqueos por texto sensible o demasiado largo.
- El reintento se ejecuta dentro de la misma ejecucion coordinada del modelo, evitando esperas por `cooldown` entre el primer intento y la recuperacion.
- Se conserva el comportamiento anterior para noticias sin senales educativas: clasificacion segura `OTROS/FUERA_DE_AMBITO` y estado `DISCARDED`.

## Pruebas y verificaciones

- Consulta PostgreSQL local de ultimos fallos `WF02_CLASSIFICATION` y detalle de `newsId=4611`.
- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest" test` OK, 11 tests.

## Nota operativa

Tras desplegar y reiniciar el backend con esta version, la siguiente ejecucion de `WF-02` deberia reintentar `newsId=4611` con contexto reducido y permitir que Gemini clasifique usando titulo, URL y resumen sin reenviar el cuerpo sensible completo.
