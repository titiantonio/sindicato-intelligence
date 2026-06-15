# 2026-06-15 - Modal editorial en content

## Fecha

2026-06-15

## Objetivo

Modificar la pantalla `/content` para que el panel editorial se muestre como modal al visualizar o editar un elemento de la tabla.

## Contexto

Intervencion alineada con Documento 30, Fase 11, y Documento 31, Sprint 11. La mejora se limita al frontend Angular y mantiene el contrato backend existente de contenido y publicaciones.

## Fase MVP

Fase 11: frontend Angular / backoffice.

## Archivos modificados

- `frontend/src/app/features/content/content-page.component.ts`
- `frontend/src/app/features/content/content-page.component.html`
- `frontend/src/app/features/content/content-page.component.scss`
- `frontend/src/app/features/content/content-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_15_modal_editorial_content.md`

## Decisiones

- El click en fila abre el modal en modo lectura.
- El boton `Editar` abre el modal en modo edicion y detiene la propagacion del click de fila.
- `Editar` queda permitido para `PENDING_REVIEW` y `APPROVED`, y bloqueado para `REJECTED` y `PUBLISHED`.
- La programacion de publicaciones se mantiene dentro del modal y solo queda activa para contenido `APPROVED`.
- No se modifica backend porque `PUT /api/v1/content/{id}` ya permite editar contenido aprobado y devolverlo a revision.

## Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/content/content-page.component.spec.ts` OK, 6 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 86 tests.
- `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto inicial, `users-page.component.scss` y `sources-page.component.scss`.
