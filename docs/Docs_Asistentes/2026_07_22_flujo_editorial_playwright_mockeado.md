# Flujo Editorial Playwright Mockeado

## Objetivo

Completar `T13.5` cubriendo con Playwright el flujo editorial MVP controlado sin IA real ni publicacion Telegram real.

## Contexto

La tarea pertenece al Sprint 13 de calidad E2E. El flujo funcional validado sigue la secuencia `Event -> Content -> Publication` y se ejecuta con API mockeada para no depender de backend, PostgreSQL, proveedores IA ni Telegram.

## Archivos modificados

- `frontend/e2e/support/mock-api.ts`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `docs/guia_playwright.md`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos creados

- `frontend/e2e/editorial-flow.mock.spec.ts`.
- `docs/Docs_Asistentes/2026_07_22_flujo_editorial_playwright_mockeado.md`.

## Decisiones tomadas

- La suite `editorial-flow.mock.spec.ts` usa `mockApi` con estado mutable por test para simular transiciones de contenido y publicaciones.
- Se valida generacion de contenido desde `/events/101` usando analisis existente y tipo `TELEGRAM_SHORT` para evitar duplicado activo del mock base.
- Se valida revision humana con aprobacion y rechazo desde `/content`.
- Se valida programacion simulada y presencia en `/publications` sin ejecutar envio Telegram.
- Se sube la version del paquete frontend a `0.0.32`.

## Pruebas y verificaciones

Verificaciones ejecutadas:

```powershell
npm.cmd run e2e -- e2e/editorial-flow.mock.spec.ts
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd audit
npm.cmd run e2e
```

Resultado:

- `npm.cmd run e2e -- e2e/editorial-flow.mock.spec.ts`: OK, 2 tests pasados.
- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests `SUCCESS`.
- `npm.cmd audit`: OK, 0 vulnerabilidades.
- `npm.cmd run e2e`: OK, 6 tests pasados y 4 omitidos por backend local no habilitado.
