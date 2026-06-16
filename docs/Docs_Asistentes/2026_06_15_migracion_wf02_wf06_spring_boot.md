# Migracion WF-02 a WF-06 a Spring Boot

## Fecha

2026-06-15

## Objetivo

Migrar las automatizaciones internas `WF-02` a `WF-06` desde n8n hacia Spring Boot, manteniendo `WF-01-Capture-News` como workflow n8n para captura RSS/XML.

## Contexto

La arquitectura del proyecto establece que n8n debe orquestar procesos externos y que la logica de negocio debe residir en Spring Boot. Los workflows `WF-02` a `WF-06` ya delegaban en endpoints backend o no eran invocados desde Angular, por lo que se consolidaron como API y jobs internos.

## Fase MVP

Sprint 12: consolidacion de automatizaciones internas en Spring Boot.

## Archivos modificados

- Backend: modulo `automation`, repositorios de noticias y analisis, seguridad y version `backend/pom.xml`.
- Frontend: `AutomationService`, modelos de automatizacion y acciones en dashboard.
- n8n: eliminado `WF-02` a `WF-06`, mantenido `WF-01`, actualizado `validate-workflows.ps1`.
- Documentacion: `Documento 09 V2.0`, `Documento 31`, `CHANGELOG.md`.

## Decisiones

- `WF-01` permanece en n8n por su responsabilidad de captura RSS/XML externa.
- Clasificacion, deteccion de eventos, analisis, contenido y publicacion pasan a Spring Boot.
- Angular llama solo a `/api/v1`, nunca a webhooks n8n.
- Los schedulers internos quedan desactivados por defecto salvo activacion explicita con propiedades `app.automation.*.enabled`.

## Pruebas o verificaciones

- Backend focal: `mvn "-Dtest=AutomationControllerTest,ProcessPendingClassificationsUseCaseTest,ProcessPendingEventDetectionUseCaseTest,ProcessPendingEventAnalysisUseCaseTest" test` OK, 8 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/core/services/automation.service.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK, 10 tests.
- Backend completo: `mvn test` OK, 217 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 94 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto en bundle inicial, `sources-page.component.scss` y `users-page.component.scss`.
- n8n: `n8n/validate-workflows.ps1` OK para `WF-01`.
