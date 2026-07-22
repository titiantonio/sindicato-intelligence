# Suite Playwright Backend Local

## Objetivo

Completar `T13.4` creando una suite Playwright separada para validar el backoffice Angular contra backend local controlado.

## Contexto

La tarea pertenece al Sprint 13 de calidad E2E. Complementa las suites mockeadas de `T13.3` con una suite opt-in que no se ejecuta por defecto para evitar dependencia permanente de PostgreSQL, backend y credenciales locales.

## Archivos modificados

- `frontend/package.json`.
- `frontend/package-lock.json`.
- `frontend/playwright.config.ts`.
- `docs/guia_playwright.md`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos creados

- `frontend/e2e/backend.local.spec.ts`.
- `docs/Docs_Asistentes/2026_07_22_suite_playwright_backend_local.md`.

## Decisiones tomadas

- La suite con backend real es opt-in mediante `E2E_BACKEND_ENABLED=true`.
- Las credenciales no se versionan; se leen desde `E2E_BACKEND_EMAIL` y `E2E_BACKEND_PASSWORD`.
- El rol esperado se declara con `E2E_BACKEND_ROLE=ADMIN` o `E2E_BACKEND_ROLE=EDITOR` para validar permisos.
- Se anade `npm.cmd run e2e:backend` para ejecutar solo la suite contra backend real.
- La suite rapida `npm.cmd run e2e` sigue pudiendo ejecutarse sin backend: la suite real queda omitida si no se habilita.
- Se sube la version del paquete frontend a `0.0.30`.
- Tras detectar timeouts intermitentes de `page.goto('/login')` con ejecucion paralela, Playwright queda limitado a un worker para estabilizar el arranque local de Angular.
- Se sube la version del paquete frontend a `0.0.31`.

## Variables de entorno

```powershell
$env:E2E_BACKEND_ENABLED = 'true'
$env:E2E_BACKEND_EMAIL = '<usuario-local>'
$env:E2E_BACKEND_PASSWORD = '<password-local>'
$env:E2E_BACKEND_ROLE = 'ADMIN'
npm.cmd run e2e:backend
```

## Pruebas y verificaciones

Comandos ejecutados para cierre:

```powershell
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd run e2e
npm.cmd run e2e:backend
```

Resultados:

- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests `SUCCESS`. Karma informa un aviso final de cierre de ChromeHeadless, sin fallo de comando.
- `npm.cmd run e2e`: OK, 4 tests pasados y 4 omitidos por backend local no habilitado.
- `npm.cmd run e2e:backend`: OK, 4 tests omitidos por backend local no habilitado.

La ejecucion real contra backend local requiere stack levantado y credenciales locales configuradas fuera del repositorio.
