# 2026-07-25 - Cuarentena WF-03 por HTTP 429 en Gemini

## Objetivo

Diagnosticar por que `WF03_EVENT_DETECTION` fallaba repetidamente con `Gemini event matching request failed with HTTP 429` para las noticias `506`, `507` y `508`, y aplicar una correccion operativa para evitar el bucle.

## Contexto

- Fase Documento 30: Fase 12, automatizaciones internas y observabilidad IA.
- Documento 31: tarea `19.55 Cuarentena de fallos repetidos WF-03 Gemini 429`.
- Las noticias `506`, `507` y `508` estaban en estado `CLASSIFIED`.
- `WF03_EVENT_DETECTION` estaba habilitado con `batch_size=3`.
- `WF03_EVENT_MATCHING` usaba proveedor `gemini`, modelo `models/gemma-4-31b-it`, `cooldown_seconds=60`.

## Diagnostico

La automatizacion cargaba siempre las mismas tres noticias clasificadas al inicio del lote. Como las tres llamadas a Gemini devolvian `HTTP 429`, las noticias no pasaban a `EVENT_MATCHED`, quedaban elegibles para la siguiente ejecucion y acumulaban fallos repetidos.

Contadores observados en `ai_operation_metrics`:

- `news_id=506`: 46 fallos.
- `news_id=507`: 43 fallos.
- `news_id=508`: 43 fallos.

## Archivos Modificados

- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessPendingEventDetectionUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/ProcessPendingEventDetectionUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se reutiliza el patron ya aplicado en WF-02: cuarentena temporal por fallos recientes consultando `ai_operation_metrics`.
- La cuarentena no cambia el estado de la noticia y no descarta contenido.
- Las noticias saltadas por cuarentena no consumen cupo de llamadas IA del lote.
- Se anade lookahead para que WF-03 pueda procesar otras noticias `CLASSIFIED` mientras las fallidas quedan temporalmente en espera.

## Verificacion

- Backend focal WF-03/scheduler: `mvnw.cmd "-Dtest=ProcessPendingEventDetectionUseCaseTest,RunAutomationWorkflowUseCaseTest" test` genero reports Surefire correctos: 11 tests, 0 fallos, 0 errores. La llamada del wrapper supero el timeout externo tras escribir los reports.
- Backend compilacion: `mvnw.cmd -q test-compile` OK.
