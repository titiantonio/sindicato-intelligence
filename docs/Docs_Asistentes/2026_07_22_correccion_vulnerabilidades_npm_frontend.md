# Correccion Vulnerabilidades NPM Frontend

## Objetivo

Corregir las vulnerabilidades detectadas en el arbol npm del frontend antes de continuar con `T13.3` de Playwright.

## Contexto

La tarea es mantenimiento de seguridad sobre el frontend Angular y la integracion Playwright del Sprint 13. No cambia reglas de dominio, backend, IA, n8n ni Telegram.

## Archivos modificados

- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- Se ejecuta primero `npm.cmd audit --json` para identificar vulnerabilidades y rutas de dependencia.
- Se ejecuta `npm.cmd audit fix` sin `--force` para aplicar correcciones no rompientes.
- No se acepta el fix forzado propuesto por npm porque degradaba `@angular/cli` a `21.0.4` frente a la rama actual `21.2.x`.
- Se anade override controlado de `@hono/node-server` a `2.0.11`, version no vulnerable, para resolver la vulnerabilidad transitiva de `@angular/cli -> @modelcontextprotocol/sdk -> @hono/node-server`.
- Se sube la version del paquete frontend a `0.0.27`.

## Pruebas y verificaciones

Comandos ejecutados en `frontend/`:

```powershell
npm.cmd audit --json
npm.cmd audit fix
npm.cmd view @modelcontextprotocol/sdk@1.26.0 dependencies --json
npm.cmd view @modelcontextprotocol/sdk@1.29.0 dependencies --json
npm.cmd install
npm.cmd audit
npm.cmd ls @hono/node-server @modelcontextprotocol/sdk @angular/cli
npm.cmd run build
npm.cmd test -- --watch=false --browsers=ChromeHeadless
npm.cmd run e2e
```

Resultado:

- `npm.cmd audit`: `found 0 vulnerabilities`.
- `npm.cmd ls`: `@hono/node-server@2.0.11 overridden` bajo `@angular/cli@21.2.19`.
- `npm.cmd run build`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 152 tests.
- `npm.cmd run e2e`: OK, 1 test Playwright.

## Observaciones

`npm install` mantiene avisos `allow-scripts` sobre paquetes con scripts de instalacion pendientes de aprobacion. No se aprueban scripts en esta intervencion porque no es necesario para resolver las vulnerabilidades ni para ejecutar build, unit tests y E2E.
