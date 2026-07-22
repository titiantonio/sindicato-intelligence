# Resolucion Allow Scripts Frontend

## Objetivo

Resolver los avisos `allow-scripts` de npm 11 en el frontend antes de continuar con `T13.3` de Playwright.

## Contexto

Tras corregir vulnerabilidades npm, `npm install` mantenia avisos de paquetes con scripts de instalacion no revisados. La intervencion se limita al frontend Angular y al toolchain de build; no cambia dominio, backend, IA, n8n ni Telegram.

## Archivos modificados

- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- No se usa `npm approve-scripts --all` para evitar aprobacion amplia.
- Se revisan rutas de dependencia y scripts declarados antes de aprobar.
- Los paquetes pendientes pertenecen al toolchain de `@angular/build`: `@parcel/watcher`, `esbuild`, `lmdb` y `msgpackr-extract`.
- Se aprueban solo esos paquetes con entradas versionadas en `allowScripts`.
- Se sube la version del paquete frontend a `0.0.28`.

## Paquetes aprobados

- `@parcel/watcher@2.5.6`.
- `esbuild@0.28.1`.
- `esbuild@0.27.2`.
- `lmdb@3.5.1`.
- `msgpackr-extract@3.0.4`.

## Pruebas y verificaciones

Comandos ejecutados en `frontend/`:

```powershell
npm.cmd approve-scripts --allow-scripts-pending --json
npm.cmd ls @parcel/watcher esbuild lmdb msgpackr-extract
npm.cmd view @parcel/watcher@2.5.6 scripts --json
npm.cmd view esbuild@0.28.1 scripts --json
npm.cmd view lmdb@3.5.1 scripts --json
npm.cmd view msgpackr-extract@3.0.4 scripts --json
npm.cmd approve-scripts @parcel/watcher esbuild lmdb msgpackr-extract
npm.cmd install
npm.cmd approve-scripts --allow-scripts-pending
npm.cmd audit
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd run e2e
```

Resultado:

- `npm.cmd install`: sin avisos `allow-scripts`.
- `npm.cmd approve-scripts --allow-scripts-pending`: `No packages with unreviewed install scripts.`
- `npm.cmd audit`: `found 0 vulnerabilities`.
- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests.
- `npm.cmd run e2e`: OK, 1 test Playwright.
