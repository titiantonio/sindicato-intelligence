# 2026-07-25 - Scroll menu lateral movil

## Objetivo

Corregir el menu lateral del backoffice en movil, que no permitia desplazamiento vertical cuando el contenido superaba la altura disponible.

## Contexto

- Fase Documento 30: Fase 11, backoffice Angular.
- Documento 31: mantenimiento correctivo posterior al Sprint 14 sobre shell, sidebar y navegacion movil.
- Skill aplicada: `sindicato-frontend-angular-backoffice`.

## Archivos modificados

- `frontend/src/app/layout/shell/shell.component.scss`
- `frontend/src/app/layout/shell/shell-modern.component.scss`
- `frontend/e2e/visual-system.mock.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_07_25_scroll_menu_lateral_movil.md`

## Decisiones tomadas

- Mantener el patron actual de drawer lateral movil sin cambiar rutas, roles ni estructura Angular.
- Habilitar scroll solo en el sidebar movil con `overflow-y: auto`, altura `100dvh`, `overscroll-behavior: contain` y desplazamiento tactil.
- Ajustar la hoja moderna del shell para conservar altura dinamica en movil.
- Anadir regresion Playwright mockeada con viewport bajo (`320x360`) para comprobar que el panel lateral acepta scroll real.

## Verificaciones

- Frontend build: `npm.cmd run build` OK.
- Playwright visual mockeado focal: `npx.cmd playwright test e2e/visual-system.mock.spec.ts -g "permite desplazar" --reporter=list` OK, 1 test.
- Playwright visual mockeado completo: `npx.cmd playwright test e2e/visual-system.mock.spec.ts --reporter=list` OK, 5 tests.
