---
name: sindicato-playwright-e2e
description: Usar para crear, revisar, mantener o ejecutar pruebas End-to-End con Playwright en el frontend Angular del backoffice. Activa esta skill cuando el usuario mencione Playwright, E2E, pruebas smoke, trazas, mocks de API, tests contra backend local, CI de frontend o validacion navegada de login, dashboard, eventos, contenido, publicaciones, fuentes, usuarios, audit o settings.
---

# Sindicato Playwright E2E

## Proposito

Guiar la integracion y evolucion de pruebas E2E Playwright para el backoffice Angular, manteniendo separacion entre pruebas mockeadas rapidas y pruebas contra backend local controlado.

Playwright complementa las pruebas unitarias Angular y backend. No sustituye Karma/Jasmine, JUnit ni Mockito.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `AGENTS.md`.
- `docs/guia_playwright.md`.
- `docs/Documentacion Proyecto/Documento 15 - Plan de Pruebas.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Ubicacion esperada

- Configuracion: `frontend/playwright.config.ts`.
- Tests E2E: `frontend/e2e`.
- Artefactos ignorados: reportes, trazas, screenshots y resultados temporales de Playwright.
- Scripts npm: `e2e`, `e2e:ui`, `e2e:headed`, `e2e:report`.

## Estrategia por fases

1. Integracion base sin tocar dominio ni backend.
2. Smoke tests mockeados que no requieran PostgreSQL ni backend.
3. Pruebas con backend local usando datos y usuarios controlados.
4. Flujo editorial MVP controlado sin IA real ni Telegram real.
5. Preparacion de CI/CD con reportes y trazas.

## Reglas de escritura de tests

- Prioriza locators accesibles: `getByRole`, `getByLabel`, `getByText`.
- Evita selectores por clases CSS salvo que no exista alternativa estable.
- No acoples tests a textos volatiles si existe un rol, label o nombre accesible estable.
- Usa nombres de tests orientados a comportamiento observable.
- Mantiene los tests pequenos: login, navegacion, permisos y flujo editorial deben separarse en casos verificables.
- No dupliques reglas de negocio en los tests; valida comportamiento de usuario y respuestas visibles.

## Seguridad

- No versiones JWT, refresh tokens, passwords reales, API keys, tokens IA, tokens n8n ni tokens Telegram.
- No ejecutes IA real ni publicaciones Telegram reales desde E2E salvo entorno seguro explicitamente preparado y documentado.
- Las suites mockeadas deben interceptar `/api/v1/**` desde Playwright.
- Las suites con backend real deben documentar requisitos de datos, usuario y servicios locales.

## Suites recomendadas

- `frontend/e2e/smoke.mock.spec.ts`: carga de login, login simulado y navegacion principal.
- `frontend/e2e/admin.mock.spec.ts`: visibilidad de rutas ADMIN con respuestas mockeadas.
- `frontend/e2e/auth.backend.spec.ts`: login real y rutas protegidas contra backend local.
- `frontend/e2e/editorial.backend.spec.ts`: flujo editorial controlado cuando existan datos semilla seguros.

## Verificacion minima

Antes de cerrar una tarea Playwright, intenta ejecutar la verificacion mas cercana:

- `npm.cmd run e2e` para suite base.
- `npm.cmd run e2e -- --project=chromium` si se quiere limitar navegador.
- `npm.cmd run e2e:report` para revisar el informe tras fallos.

Si no se ejecutan pruebas, documenta el motivo en `docs/Docs_Asistentes`.

## Checklist de cierre

- El Documento 31 marca la tarea correspondiente del Sprint 13.
- La guia `docs/guia_playwright.md` queda actualizada si cambian comandos o estructura.
- Los tests no contienen secretos ni dependencias de Telegram/IA real.
- Los comandos ejecutados y resultados quedan registrados en Docs_Asistentes.
