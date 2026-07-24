# Fallback JSON incompleto WF-04 evento 25

Fecha: 2026-07-24

## Objetivo

Comprobar por que `WF04_ANALYSIS` falla para el evento 25 y corregir el bloqueo si procede.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a Fase 12 del Documento 30: automatizaciones internas, IA y observabilidad.

## Diagnostico

- `event_id=25` es un evento de `OPOSICIONES` con importancia `CRITICAL`.
- Tiene una sola noticia asociada, `news_id=221`, de ANPE Andalucia.
- No es un caso PDF: la URL es una pagina HTML de ANPE.
- El contexto capturado es muy breve: resumen y contenido de 53 caracteres.
- Las metricas IA registraban tres fallos `WF04_ANALYSIS` para `event_id=25` con `Gemini response does not contain a JSON object`.
- El log muestra que Gemini empieza a devolver un JSON, pero la salida queda incompleta y degenera en repeticion de texto.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProviderTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

## Decisiones tomadas

- Se mantiene el reintento normal ante respuestas IA invalidas.
- Si tras el reintento Gemini devuelve un JSON iniciado pero incompleto, se persiste un analisis conservador con sufijo de modelo `:conservative-json-fallback`.
- Si la respuesta no contiene ningun JSON, se mantiene el error para no convertir cualquier salida arbitraria en analisis.

## Pruebas y verificaciones

- `mvn "-Dtest=GeminiAnalysisAIProviderTest" test`: OK, 6 tests.
- `mvn "-Dtest=ProcessPendingEventAnalysisUseCaseTest,RunAutomationWorkflowUseCaseTest,ProcessDueAutomationWorkflowsUseCaseTest" test`: OK, 13 tests.
