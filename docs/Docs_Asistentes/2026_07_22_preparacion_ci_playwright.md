# Preparacion CI Playwright

## Objetivo

Completar `T13.6` dejando preparada la ejecucion futura de Playwright en CI/CD sin crear aun un pipeline concreto.

## Contexto

La tarea pertenece al Sprint 13 de calidad E2E. El objetivo es separar la suite mockeada rapida de la suite contra backend local y dejar artefactos de diagnostico utiles para ejecuciones headless.

## Archivos modificados

- `frontend/playwright.config.ts`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `docs/guia_playwright.md`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos creados

- `docs/Docs_Asistentes/2026_07_22_preparacion_ci_playwright.md`.

## Decisiones tomadas

- `npm.cmd run e2e:mock` ejecuta solo suites sin backend ni PostgreSQL.
- `npm.cmd run e2e:ci` queda como alias de la suite mockeada rapida para una integracion CI inicial.
- `npm.cmd run e2e:backend` permanece separado y opt-in mediante variables `E2E_BACKEND_*`.
- Playwright conserva un unico worker para estabilidad local y CI inicial.
- En CI se usa reporter `list` mas HTML con apertura deshabilitada.
- Se habilitan trazas en primer reintento, screenshots en fallo y videos retenidos en fallo.
- Se sube la version del paquete frontend a `0.0.33`.

## Pruebas y verificaciones

Verificaciones ejecutadas:

```powershell
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd audit
npm.cmd run e2e:mock
npm.cmd run e2e:ci
npm.cmd run e2e
```

Resultados:

- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests `SUCCESS`.
- `npm.cmd audit`: OK, 0 vulnerabilidades.
- `npm.cmd run e2e:mock`: OK, 6 tests pasados.
- `npm.cmd run e2e:ci`: OK, 6 tests pasados.
- `npm.cmd run e2e`: OK, 6 tests pasados y 4 omitidos por backend local no habilitado.
