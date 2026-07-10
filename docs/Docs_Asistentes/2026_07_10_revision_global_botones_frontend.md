# Revision global de botones frontend

## Fecha

2026-07-10

## Objetivo

Corregir el comportamiento visual de los botones del backoffice cuando cambian a estados de carga o deshabilitados, especialmente el boton de login al pasar de `Entrar` a `Accediendo...`.

## Contexto

Tarea de refinamiento visual posterior a Sprint 12, ubicada en Fase 11 Frontend Angular. La revision detecto mezcla de botones PrimeNG con contenido interpolado y reglas SCSS locales demasiado amplias que podian alterar altura, alineacion, iconos o spinner.

## Fase MVP

Fase 11: Frontend Angular.

## Archivos modificados

- `frontend/src/styles.scss`
- `frontend/src/app/features/auth/login/login-page.component.html`
- `frontend/src/app/features/auth/forgot-password/forgot-password-page.component.html`
- `frontend/src/app/features/auth/reset-password/reset-password-page.component.html`
- `frontend/src/app/features/auth/change-password/change-password-page.component.html`
- `frontend/src/app/features/dashboard/dashboard-page.component.html`
- `frontend/src/app/features/events/event-detail-page.component.html`
- `frontend/src/app/features/users/users-page.component.html`
- `frontend/src/app/features/sources/sources-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.scss`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se centralizo el contrato visual de `button[pButton]` y `.p-button` en estilos globales.
- Se reemplazo el texto interpolado dentro de botones con carga por `[label]` para que PrimeNG gestione de forma estable icono, spinner y etiqueta.
- No se cambiaron servicios, navegacion, permisos ni contratos REST.
- En settings se limito la regla local de `button` a botones no PrimeNG para evitar pisar el layout interno del componente.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit`: OK.
- Tests focales frontend de auth, dashboard, detalle de evento, usuarios, fuentes y settings: OK, 57 tests.
- `npm.cmd run build`: OK. Bundle inicial: `528.21 kB`.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 147 tests.
- Persiste el warning no bloqueante ya conocido de cierre lento de ChromeHeadless.
