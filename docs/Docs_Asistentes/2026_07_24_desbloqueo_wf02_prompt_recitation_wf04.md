# Desbloqueo WF-02 y prompt RECITATION WF-04

Fecha: 2026-07-24

## Objetivo

Comprobar por que WF-04 seguia fallando en el evento 17 y si las automatizaciones habian quedado paradas tras varios fallos.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a Fase 12 del Documento 30: automatizaciones internas, IA y observabilidad.

## Diagnostico

- `WF02_CLASSIFICATION` estaba bloqueado con `running=true` desde `2026-07-24 16:49:21 UTC`.
- `WF04_ANALYSIS` no estaba bloqueado en `running`, pero fallaba repetidamente para `event_id=17` con `finishReason=RECITATION`.
- Tras el reinicio, WF-04 estaba vencido pero el scheduler seguia ocupando `scheduling-1` con WF-02/WF-03 y cooldown del mismo modelo IA.
- El backend local estaba en ejecucion mediante `mvn spring-boot:run`; las correcciones de codigo requieren reinicio del backend para entrar en vigor.
- El evento 17 contiene la noticia 44, cuya URL principal es un PDF BOJA oficial.
- WF-04 recibe URL y metadatos/resumen de la noticia, pero no adjunta ni extrae el contenido binario del PDF.
- En este caso, el `RECITATION` es coherente con el titulo oficial largo repetido como titulo de evento y titulo de noticia, no con un PDF adjunto.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProviderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se desbloqueo operativamente `WF02_CLASSIFICATION` en PostgreSQL con `running=false` y `next_run_at=now()`.
- Se reforzo el reintento de WF-04 por `RECITATION` con un prompt reducido que evita reenviar titulos oficiales literales duplicados.
- El prompt reducido conserva tema operativo, categoria, importancia, URL oficial, resumen disponible y fecha.
- Se anadio un fallback conservador de analisis si el reintento reducido evita `RECITATION` pero Gemini devuelve JSON incompleto.
- Se corrigio la reprogramacion inmediata de `WF02_CLASSIFICATION` y `WF03_EVENT_DETECTION` para no retrasar `WF04_ANALYSIS` cuando hay otro workflow vencido y todos comparten cooldown del mismo modelo IA.
- No se implementa extraccion ni adjunto automatico de PDF a Gemini en esta intervencion, por ser una ampliacion funcional distinta.

## Pruebas y verificaciones

- `mvn "-Dtest=GeminiAnalysisAIProviderTest,ProcessPendingEventAnalysisUseCaseTest" test`: OK, 10 tests.
- `mvn "-Dtest=RunAutomationWorkflowUseCaseTest,ProcessDueAutomationWorkflowsUseCaseTest,GeminiAnalysisAIProviderTest,ProcessPendingEventAnalysisUseCaseTest" test`: OK, 18 tests.
