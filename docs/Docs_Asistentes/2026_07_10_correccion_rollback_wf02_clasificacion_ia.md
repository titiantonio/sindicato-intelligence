# Correccion rollback WF-02 clasificacion IA

Fecha: 2026-07-10

## Objetivo

Corregir WF-02 para que un fallo puntual del proveedor IA en una noticia no provoque rollback del lote completo ni deje `next_run_at` vencido.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a la Fase 12 del Documento 30: automatizaciones internas, observabilidad IA y configuracion ADMIN.

La incidencia observada era que una noticia con fallo de Gemini hacia que las noticias clasificadas correctamente no quedaran persistidas, aunque existieran metricas IA de exito. El efecto operativo era la repeticion del mismo lote porque el workflow seguia vencido.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se mantiene la politica de reintento normal: la noticia que falla permanece `CAPTURED` y se reintenta en la siguiente ejecucion programada.
- No se anaden columnas, migraciones, estados nuevos, backoff ni contador de reintentos.
- Se elimina la transaccion global del orquestador y se usan transacciones cortas para cambios de estado del workflow y auditoria.
- `ClassifyNewsUseCase` conserva su transaccion individual por noticia.

## Verificacion

- `mvn -Dtest=RunAutomationWorkflowUseCaseTest test`: OK, 3 tests.
- `mvn "-Dtest=ClassifyNewsUseCaseTest,RunAutomationWorkflowUseCaseTest" test`: OK, 10 tests.
- `mvn clean compile`: OK.
- `mvn "-Dtest=IntelligenceApplicationTests" test`: OK, 1 test.
