# 2026-07-25 - Contencion de reprogramacion inmediata ante Gemini 429 en WF-03

## Objetivo

Comprobar por que `WF03_EVENT_MATCHING` seguia fallando con `Gemini event matching request failed with HTTP 429` en noticias distintas tras aplicar cuarentena de fallos repetidos, y contener la amplificacion de llamadas.

## Contexto

- Fase Documento 30: Fase 12, automatizaciones internas y observabilidad IA.
- Documento 31: tarea `19.56 Contencion de reprogramacion inmediata ante 429 Gemini`.
- Workflows afectados: `WF02_CLASSIFICATION` y `WF03_EVENT_DETECTION`.

## Diagnostico

La cuarentena de WF-03 ya estaba actuando: `WF03_EVENT_DETECTION` registro `last_skipped_count=3`. Sin embargo, el mismo lote tuvo tambien `last_failed_count=2` por `HTTP 429` y quedo reprogramado practicamente de inmediato.

Datos observados:

- `WF02_CLASSIFICATION`, `WF03_EVENT_MATCHING`, `WF04_ANALYSIS` y `WF05_CONTENT` usan `models/gemma-4-31b-it`.
- Todos tienen `cooldown_seconds=60`.
- En las ultimas horas, `WF03_EVENT_MATCHING` acumulo fallos `HTTP 429` en varias noticias, incluyendo `506`, `507`, `508`, `511` y `526`.
- `WF02_CLASSIFICATION` tambien mostro fallos `HTTP 429`, senal de saturacion/cuota del proveedor y no de una noticia concreta.

## Archivos Modificados

- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCaseTest.java`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- La reprogramacion inmediata de `WF-02` y `WF-03` queda limitada a lotes completos sin fallos.
- Si Gemini devuelve `HTTP 429`, se respeta el intervalo ordinario del workflow para no amplificar la saturacion.
- Se conserva el cooldown por modelo y la cuarentena por entidad; no se cambia dominio, prompts ni proveedor IA.

## Verificacion

Backend focal WF-03/scheduler: `mvnw.cmd "-Dtest=ProcessPendingEventDetectionUseCaseTest,RunAutomationWorkflowUseCaseTest" test` OK, 12 tests, 0 fallos, 0 errores.
