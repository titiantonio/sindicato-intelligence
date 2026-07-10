# Correccion ResultSet cerrado en automatizaciones

Fecha: 2026-07-10

## Objetivo

Corregir el error `A problem occurred in the SQL executor : Error advancing (next) ResultSet position [This ResultSet is closed.]` detectado en la ventana de automatizaciones de configuracion y en las ejecuciones programadas.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a la Fase 12 del Documento 30: automatizaciones internas y configuracion ADMIN.

El error aparecia en WF-02 y WF-03 al consultar noticias pendientes desde `JpaNewsRepository.findByStatus(...)`. La revision adicional detecto que WF-04 podia verse afectado por el mismo patron en consultas de eventos desde `JpaEventRepository`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/infrastructure/JpaNewsRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/JpaEventRepository.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se sustituyen usos de `getResultStream()` por `getResultList().stream()` en las consultas implicadas en WF-02, WF-03 y WF-04.
- No se cambian contratos REST, estados de dominio, configuracion de intervalos ni migraciones Flyway.
- WF-05 y WF-06 se revisan como impacto colateral; no comparten el mismo arranque fuera de transaccion que causaba el fallo.

## Verificacion

- `mvn "-Dtest=JpaNewsRepositoryTest,RunAutomationWorkflowUseCaseTest,ProcessPendingEventAnalysisUseCaseTest" test`: OK, 10 tests.
