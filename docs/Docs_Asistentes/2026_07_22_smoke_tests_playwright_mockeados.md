# Smoke Tests Playwright Mockeados

## Objetivo

Completar `T13.3` del Sprint 13 creando smoke tests Playwright mockeados que validan login, navegacion editorial y rutas ADMIN sin backend ni PostgreSQL.

## Contexto

La tarea pertenece al Sprint 13 de calidad E2E Playwright. Su objetivo es disponer de una suite rapida y aislada para el backoffice Angular antes de abordar pruebas contra backend real en `T13.4`.

## Archivos modificados

- `frontend/package.json`.
- `frontend/package-lock.json`.
- `frontend/e2e/smoke.mock.spec.ts`.
- `frontend/e2e/admin.mock.spec.ts`.
- `docs/guia_playwright.md`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos creados

- `frontend/e2e/support/mock-api.ts`.
- `frontend/e2e/smoke.mock.spec.ts`.
- `frontend/e2e/admin.mock.spec.ts`.
- `docs/Docs_Asistentes/2026_07_22_smoke_tests_playwright_mockeados.md`.

## Decisiones tomadas

- Las suites mockeadas interceptan `/api/v1/**` con Playwright.
- Los usuarios, tokens y datos son ficticios de E2E y no contienen secretos reales.
- Se valida login simulado con rol `EDITOR`, navegacion por dashboard, eventos, contenido y publicaciones.
- Se valida que `EDITOR` no ve rutas ADMIN y que el guard redirige `/users` a `/dashboard`.
- Se valida login simulado con rol `ADMIN` y navegacion por settings, fuentes, usuarios y auditoria.
- Se sube la version del paquete frontend a `0.0.29`.

## Documento 31

Tarea completada:

- `T13.3`: crear smoke tests E2E mockeados.

Tarea siguiente:

- `T13.4`: crear suite E2E contra backend local.

## Pruebas y verificaciones

Comandos ejecutados en `frontend/`:

```powershell
npm.cmd install
npm.cmd audit
npm.cmd approve-scripts --allow-scripts-pending
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd run e2e
```

Resultado:

- `npm.cmd install`: OK, sin avisos `allow-scripts`.
- `npm.cmd audit`: `found 0 vulnerabilities`.
- `npm.cmd approve-scripts --allow-scripts-pending`: sin paquetes pendientes.
- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests.
- `npm.cmd run e2e`: OK, 4 tests Playwright.
