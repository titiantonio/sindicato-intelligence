# Disparo rapido WF-03 tras clasificacion

## Fecha

2026-07-11

## Objetivo

Reducir la latencia entre una noticia clasificada por `WF-02` y su asociacion o creacion de evento en `WF-03`, sin devolver responsabilidad a n8n ni acoplar la clasificacion al matching de eventos.

## Contexto

El sistema ya tenia `WF-03_EVENT_DETECTION` migrado a Spring Boot y ejecutado por scheduler configurable. Operativamente, una noticia podia quedar `CLASSIFIED` esperando al intervalo ordinario antes de asociarse a evento.

## Fase MVP

Fase 12: optimizacion IA, automatizaciones internas, observabilidad y configuracion ADMIN. Mejora posterior sobre `WF-02` y `WF-03`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifiedNewsFollowUpPort.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/infrastructure/EventDetectionAfterClassificationAdapter.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RequestImmediateAutomationWorkflowRunUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/domain/AutomationWorkflowSetting.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/infrastructure/AutomationWorkflowScheduler.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RequestImmediateAutomationWorkflowRunUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se mantiene el scheduler de Spring Boot como ejecutor real y respaldo.
- La clasificacion no ejecuta `DetectEventUseCase` directamente: solo solicita adelantar `WF03_EVENT_DETECTION`.
- Se usa un puerto `ClassifiedNewsFollowUpPort` para evitar dependencia directa de `classification` hacia `automation`.
- Las noticias descartadas por clasificacion no activan deteccion de eventos.
- Si `WF-03` procesa un lote completo, se reprograma inmediatamente para drenar pendientes.
- El delay por defecto del scheduler backend de automatizaciones baja de 30s a 5s.

## Tareas Documento 31

- Actualizada la seccion `19.32 Disparo rapido WF-03 tras clasificacion - 2026-07-11`.
- Marcadas como completadas las subtareas de puerto, solicitud inmediata, exclusiones de descartadas, drenaje de lotes completos y versionado backend.

## Pruebas o verificaciones

- `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest,RunAutomationWorkflowUseCaseTest,RequestImmediateAutomationWorkflowRunUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test` OK, 18 tests.
- `mvnw.cmd "-Dtest=IntelligenceApplicationTests" test` OK, 1 test.
