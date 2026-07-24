# Fecha

2026-07-24

# Objetivo

Hacer visible para ADMIN y EDITOR que existen eventos importantes ya analizados por IA y pendientes de generar contenido/publicacion.

# Contexto

La automatizacion WF-04 analiza automaticamente eventos importantes. El dashboard mostraba eventos prioritarios pendientes de analisis, pero no destacaba los eventos con analisis vigente y sin contenido activo, por lo que el equipo editorial podia no detectar que debia ejecutar WF-05.

# Fase MVP

Fase 11/12 del Documento 30: backoffice Angular, dashboard operativo, automatizaciones internas y configuracion ADMIN.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/EventEditorialStatus.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventEditorialStatusResolver.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/infrastructure/JpaDashboardSnapshotQueryRepository.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/event/api/EventControllerTest.java`
- `frontend/src/app/features/dashboard/dashboard-page.component.ts`
- `frontend/src/app/features/dashboard/dashboard-page.component.html`
- `frontend/src/app/features/dashboard/dashboard-page.component.scss`
- `frontend/src/app/features/dashboard/dashboard-page.component.spec.ts`
- `backend/pom.xml`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se anadio el estado editorial `ANALYZED_PENDING_CONTENT`.
- La notificacion se implementa como aviso interno persistente en el dashboard, no como email/Telegram externo.
- El dashboard incluye eventos `HIGH` o `CRITICAL` con analisis vigente y sin contenido activo.
- Se excluyen eventos con contenido `GENERATED`, `PENDING_REVIEW`, `APPROVED` o `PUBLISHED` para evitar duplicidades.
- La accion del dashboard genera contenido `TELEGRAM_POST` informativo mediante el endpoint existente de WF-05 y navega al detalle del contenido generado para revision humana.

# Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK, 9 tests.
- `mvnw.cmd clean test-compile` OK.
- `mvnw.cmd "-Dtest=DashboardControllerTest" test` OK, 3 tests.
- `mvnw.cmd "-Dtest=EventControllerTest#exposesEditorialStatusForAnalyzedAndPublishedEvents" test` OK, 1 test.

# Observaciones

Durante una ejecucion amplia inicial, `EventControllerTest` completo mostro un fallo no relacionado en `detectsEventCreatingNewEventWhenNoMatchExists` por datos reales locales que alteraban el matching esperado. Se verifico de forma focal el caso afectado por esta intervencion.
