# Playwright Base Frontend

## Objetivo

Completar `T13.2` del Sprint 13 integrando la base tecnica de Playwright en el frontend Angular.

## Contexto

La tarea pertenece al Sprint 13 de calidad E2E Playwright. Encaja tras la Fase 11 del Documento 30 porque valida el backoffice Angular sin cambiar dominio, backend, IA ni publicaciones Telegram.

## Archivos modificados

- `frontend/package.json`.
- `frontend/package-lock.json`.
- `frontend/.gitignore`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/guia_playwright.md`.
- `CHANGELOG.md`.

## Archivos creados

- `frontend/playwright.config.ts`.
- `frontend/e2e/app-startup.spec.ts`.
- `docs/Docs_Asistentes/2026_07_22_playwright_base_frontend.md`.

## Decisiones tomadas

- Se instala `@playwright/test` como dependencia de desarrollo del frontend.
- Se sube la version del paquete frontend a `0.0.26`.
- La primera configuracion usa solo Chromium para mantener la verificacion local rapida.
- `playwright.config.ts` arranca Angular automaticamente con `npm run start -- --host 127.0.0.1`.
- Se versiona una prueba minima de arranque sobre `/login` para que `npm.cmd run e2e` sea verificable desde `T13.2`.
- Los smoke tests mockeados completos quedan para `T13.3`.

## Documento 31

Tarea completada:

- `T13.2`: integrar Playwright base en `frontend/`.

Tarea siguiente:

- `T13.3`: crear smoke tests E2E mockeados.

## Pruebas y verificaciones

Comandos ejecutados en `frontend/`:

```powershell
npm.cmd install --save-dev @playwright/test
npm.cmd run e2e
npx.cmd playwright install chromium
npm.cmd run e2e
```

Resultado:

- Primera ejecucion: fallo esperado por navegador Chromium de Playwright no instalado en la maquina local.
- Instalado Chromium con `npx.cmd playwright install chromium`.
- Segunda ejecucion: `1 passed` en Chromium.

## Observaciones

`npm install` informa vulnerabilidades en el arbol npm. No se ejecuta `npm audit fix --force` porque podria introducir cambios rompientes fuera del alcance de `T13.2`.
