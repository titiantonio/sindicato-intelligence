# 2026-06-15 - Tablas backoffice con paginacion, filtros y ordenacion

## Objetivo

Extender a las tablas del backoffice el mismo criterio aplicado a usuarios: paginacion, selector de filas por pagina, filtros por columna y ordenacion por los campos visibles.

## Contexto

Intervencion alineada con Documento 30, Fase 11, y Documento 31, Sprint 11. La paginacion se implementa en frontend sobre los listados ya disponibles para evitar ampliar contratos REST en esta mejora correctiva.

## Archivos modificados

- `frontend/src/app/features/sources/sources-page.component.ts`
- `frontend/src/app/features/sources/sources-page.component.html`
- `frontend/src/app/features/sources/sources-page.component.scss`
- `frontend/src/app/features/sources/sources-page.component.spec.ts`
- `frontend/src/app/features/events/events-page.component.ts`
- `frontend/src/app/features/events/events-page.component.html`
- `frontend/src/app/features/events/events-page.component.scss`
- `frontend/src/app/features/events/events-page.component.spec.ts`
- `frontend/src/app/features/audit/audit-page.component.ts`
- `frontend/src/app/features/audit/audit-page.component.html`
- `frontend/src/app/features/audit/audit-page.component.scss`
- `frontend/src/app/features/content/content-page.component.ts`
- `frontend/src/app/features/content/content-page.component.html`
- `frontend/src/app/features/content/content-page.component.scss`
- `frontend/src/app/features/events/event-detail-page.component.ts`
- `frontend/src/app/features/events/event-detail-page.component.html`
- `frontend/src/app/features/events/event-detail-page.component.scss`
- `frontend/src/app/features/dashboard/dashboard-page.component.ts`
- `frontend/src/app/features/dashboard/dashboard-page.component.html`
- `frontend/src/app/features/dashboard/dashboard-page.component.scss`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_15_tablas_backoffice_paginacion_filtros.md`

## Decisiones

- Se mantiene paginacion local porque los volumenes esperados del backoffice MVP son bajos y los endpoints actuales ya devuelven listados suficientes.
- La pagina actual se reinicia al cambiar filtros, orden o numero de filas para evitar estados vacios inconsistentes.
- Auditoria mantiene paginacion y ordenacion separadas por pestana para no mezclar estado entre auditoria de usuarios y editorial.
- Publicaciones no se modifica en esta iteracion porque la pantalla actual usa tarjetas, no una tabla.

## Documento 31

- Anadiada la seccion `16.27 Paginacion, filtros y ordenacion de tablas backoffice - 2026-06-15`.

## Pruebas y verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/sources/sources-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/audit/audit-page.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK, 17 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 80 tests.
- `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto CSS en `sources-page.component.scss` y `users-page.component.scss`.
