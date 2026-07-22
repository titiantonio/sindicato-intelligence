# Guia Playwright

## Objetivo

Playwright se usara para pruebas End-to-End del backoffice Angular. Su objetivo es validar flujos navegados reales o simulados que no quedan cubiertos por unit tests: login, rutas protegidas, roles, dashboard, eventos, contenido, publicaciones, fuentes, usuarios, audit y settings.

Playwright no sustituye Karma/Jasmine ni las pruebas backend JUnit/Mockito.

## Ubicacion

La integracion prevista vive dentro de `frontend/`:

```text
frontend/
  playwright.config.ts
  e2e/
    app-startup.spec.ts
    smoke.mock.spec.ts
    admin.mock.spec.ts
    editorial-flow.mock.spec.ts
    backend.local.spec.ts
```

## Fases de integracion

1. `T13.1`: documentacion, reglas, skill y backlog.
2. `T13.2`: instalacion de Playwright, configuracion y scripts npm.
3. `T13.3`: smoke tests mockeados sin backend ni PostgreSQL.
4. `T13.4`: tests contra backend local controlado.
5. `T13.5`: flujo editorial MVP controlado sin IA real ni Telegram real.
6. `T13.6`: preparacion para CI/CD, reportes y trazas.

## Comandos

Los scripts disponibles desde `T13.2` son:

```powershell
cd frontend
npm.cmd run e2e
npm.cmd run e2e:mock
npm.cmd run e2e:backend
npm.cmd run e2e:ci
npm.cmd run e2e:ui
npm.cmd run e2e:headed
npm.cmd run e2e:report
```

Si es la primera vez que se ejecuta Playwright en la maquina local, instala el navegador Chromium usado por la suite base:

```powershell
cd frontend
npx.cmd playwright install chromium
```

Uso esperado:

- `e2e`: ejecuta la suite headless.
- `e2e:mock`: ejecuta solo suites mockeadas sin backend ni PostgreSQL.
- `e2e:backend`: ejecuta solo la suite contra backend local, si esta habilitada por variables de entorno.
- `e2e:ci`: alias de la suite mockeada rapida para integracion CI inicial.
- `e2e:ui`: abre el modo interactivo de Playwright.
- `e2e:headed`: ejecuta mostrando el navegador.
- `e2e:report`: abre el ultimo informe HTML.

## Suites mockeadas

Las suites mockeadas deben poder ejecutarse sin backend ni PostgreSQL.

Reglas:

- Interceptar `/api/v1/**` con Playwright.
- Simular login con respuestas controladas.
- Usar datos pequenos y expresivos.
- Cubrir navegacion principal y permisos visibles.
- No depender de credenciales reales.

Estas suites son las recomendadas para ejecucion rapida local y futura CI inicial.

Desde `T13.6`, el comando recomendado para CI inicial es:

```powershell
cd frontend
npm.cmd run e2e:ci
```

Este comando ejecuta solo `app-startup`, `smoke.mock`, `admin.mock` y `editorial-flow.mock`; no incluye `backend.local.spec.ts`.

## Suites con backend real

Las suites con backend real deben ejecutarse solo con entorno local controlado.

Requisitos previstos:

- PostgreSQL local levantado.
- Backend Spring Boot local en `http://localhost:8080`.
- Frontend Angular local en `http://localhost:4200` o levantado por Playwright.
- Usuario de pruebas documentado y seguro.
- Datos semilla conocidos o preparacion previa controlada.

No se deben versionar tokens ni passwords reales en los tests.

Desde `T13.4`, la suite de backend real vive en:

```text
frontend/e2e/backend.local.spec.ts
```

Por seguridad, esta suite es opt-in. Si no se define `E2E_BACKEND_ENABLED=true`, queda omitida y no bloquea `npm.cmd run e2e`.

Variables requeridas para ejecutarla:

```powershell
$env:E2E_BACKEND_ENABLED = 'true'
$env:E2E_BACKEND_EMAIL = '<usuario-local>'
$env:E2E_BACKEND_PASSWORD = '<password-local>'
$env:E2E_BACKEND_ROLE = 'ADMIN'
npm.cmd run e2e:backend
```

Valores permitidos de `E2E_BACKEND_ROLE`:

```text
ADMIN
EDITOR
```

La suite valida login real, rutas protegidas y navegacion comun. Si el rol es `ADMIN`, valida tambien settings, fuentes, usuarios y auditoria. Si el rol es `EDITOR`, valida que las rutas ADMIN no aparecen y que `/users` redirige a `/dashboard`.

## Restricciones de seguridad

- No ejecutar IA real desde E2E.
- No publicar en Telegram real desde E2E.
- No versionar JWT, refresh tokens, passwords reales, API keys ni tokens externos.
- No capturar trazas con datos sensibles si se van a compartir.
- No confiar en controles frontend como autorizacion efectiva; los roles deben validarse tambien en backend cuando aplique.

## Buenas practicas de selectores

Preferir:

```typescript
page.getByRole('button', { name: 'Iniciar sesion' })
page.getByLabel('Email')
page.getByText('Dashboard')
```

Evitar salvo necesidad:

```typescript
page.locator('.btn-primary')
page.locator('div:nth-child(2) > span')
```

## Artefactos

Playwright puede generar:

- Informe HTML.
- Trazas.
- Screenshots.
- Videos.
- Resultados temporales.

Estos artefactos deben ignorarse en Git salvo decision explicita de versionar ejemplos documentales.

Configuracion actual de diagnostico:

- Informe HTML en `frontend/playwright-report`.
- Resultados temporales en `frontend/test-results`.
- Trazas en primer reintento.
- Screenshot solo en fallo.
- Video retenido solo en fallo.

## Configuracion base actual

Desde `T13.2`, `frontend/playwright.config.ts` arranca Angular automaticamente con:

```text
npm run start -- --host 127.0.0.1
```

La suite base usa Chromium y `baseURL` en `http://127.0.0.1:4200`.

Desde `T13.4`, Playwright usa un unico worker por defecto para evitar timeouts intermitentes al arrancar y servir Angular en entornos locales con recursos limitados.

Desde `T13.6`, en CI Playwright usa reporter `list` mas HTML con `open: never`. La suite mockeada se ejecuta con `npm.cmd run e2e:ci`; la suite de backend real queda separada y sigue siendo opt-in.

El primer test versionado es `frontend/e2e/app-startup.spec.ts`, que valida que la pantalla `/login` carga y muestra los controles basicos de acceso.

Desde `T13.3` existen tambien suites mockeadas que interceptan `/api/v1/**` y no requieren backend ni PostgreSQL:

```text
frontend/e2e/support/mock-api.ts
frontend/e2e/smoke.mock.spec.ts
frontend/e2e/admin.mock.spec.ts
frontend/e2e/editorial-flow.mock.spec.ts
```

Estas suites cubren login simulado, navegacion editorial principal, ocultacion de rutas ADMIN para `EDITOR` y navegacion de pantallas ADMIN para `ADMIN`.

Desde `T13.5`, `editorial-flow.mock.spec.ts` cubre de forma simulada el flujo `evento -> contenido -> revision humana -> publicacion programada`, incluyendo aprobacion y rechazo de contenido sin ejecutar IA real ni Telegram real.

## Criterio de cierre por fase

Cada fase debe quedar registrada en el Documento 31 y en `docs/Docs_Asistentes`, indicando comandos ejecutados y resultado.

Si una prueba no se ejecuta, debe documentarse el motivo.
