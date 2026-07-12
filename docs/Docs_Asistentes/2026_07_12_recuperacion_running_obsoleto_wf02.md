# Recuperacion running obsoleto WF-02

## Fecha

2026-07-12

## Objetivo

Diagnosticar y corregir que `WF02_CLASSIFICATION` no arrancara automaticamente tras el reinicio mientras `WF03_EVENT_DETECTION` si habia drenado las noticias clasificadas pendientes.

## Contexto

Tras consultar `automation_workflow_settings`, `WF02_CLASSIFICATION` aparecia habilitado pero persistido con `running=true` desde una ejecucion anterior. Existian 50 noticias `CAPTURED`, por lo que habia trabajo pendiente y el scheduler lo estaba saltando por considerar el workflow en ejecucion.

## Fase MVP

Fase 12: automatizaciones internas backend, observabilidad operativa y configuracion ADMIN.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/automation/application/RecoverStaleAutomationWorkflowsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessDueAutomationWorkflowsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/domain/AutomationWorkflowSetting.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RecoverStaleAutomationWorkflowsUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/ProcessDueAutomationWorkflowsUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se recuperan automaticamente workflows habilitados que lleven `running=true` mas de `app.automation.stale-running-timeout-minutes` minutos.
- El timeout por defecto es 30 minutos para no interferir en ejecuciones reales largas.
- La recuperacion marca el workflow como fallo operativo recuperado, pone `running=false` y deja `nextRunAt=now` para reintento inmediato por scheduler.
- Se mantiene el scheduler como mecanismo de ejecucion; no se introduce cola nueva ni dependencia de n8n.
- Se desbloqueo manualmente la BD local para resolver el incidente activo antes del siguiente despliegue.

## Tareas Documento 31

- Actualizada la seccion `19.33 Recuperacion de automatizaciones bloqueadas en running - 2026-07-12`.
- Marcadas como completadas las subtareas de diagnostico, recuperacion automatica, integracion en scheduler, desbloqueo local y versionado backend.

## Pruebas o verificaciones

- `mvnw.cmd "-Dtest=RecoverStaleAutomationWorkflowsUseCaseTest,ProcessDueAutomationWorkflowsUseCaseTest,RunAutomationWorkflowUseCaseTest,ClassifyNewsUseCaseTest,IntelligenceApplicationTests" test` OK, 18 tests.
- Consulta PostgreSQL local: `WF02_CLASSIFICATION` paso de `running=true` a `running=false`, `next_run_at=now`.
- Tras 12 segundos, el backend ejecuto `WF02_CLASSIFICATION`: 10 procesadas, 2 exitos, 8 fallos IA recuperables, y las noticias `CAPTURED` bajaron de 50 a 48.
