# Correccion ResultSet cerrado WF-04

Fecha: 2026-07-24

## Objetivo

Diagnosticar y corregir el fallo `ResultSet is closed` que impedia ejecutar `WF04_ANALYSIS` desde el scheduler.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a la Fase 12 del Documento 30: automatizaciones internas, IA y observabilidad.

El log adjunto muestra que el fallo ocurre antes de llamar al proveedor IA, al consultar el ultimo analisis del evento desde `ProcessPendingEventAnalysisUseCase`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/JpaEventAIAnalysisRepository.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/JpaEventAIAnalysisRepositoryTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se sustituyen `getResultStream()` por `getResultList().stream()` en las consultas de analisis por evento.
- No se cambian contratos REST, reglas de dominio, migraciones Flyway ni configuracion de automatizaciones.
- Se mantiene WF-04 en Spring Boot y n8n no asume logica de negocio.

## Pruebas y verificaciones

- `mvn "-Dtest=JpaEventAIAnalysisRepositoryTest,ProcessPendingEventAnalysisUseCaseTest" test`: OK, 7 tests.
