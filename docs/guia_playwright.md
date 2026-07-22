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
    smoke.mock.spec.ts
    admin.mock.spec.ts
    auth.backend.spec.ts
    editorial.backend.spec.ts
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

## Suites con backend real

Las suites con backend real deben ejecutarse solo con entorno local controlado.

Requisitos previstos:

- PostgreSQL local levantado.
- Backend Spring Boot local en `http://localhost:8080`.
- Frontend Angular local en `http://localhost:4200` o levantado por Playwright.
- Usuario de pruebas documentado y seguro.
- Datos semilla conocidos o preparacion previa controlada.

No se deben versionar tokens ni passwords reales en los tests.

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

## Configuracion base actual

Desde `T13.2`, `frontend/playwright.config.ts` arranca Angular automaticamente con:

```text
npm run start -- --host 127.0.0.1
```

La suite base usa Chromium y `baseURL` en `http://127.0.0.1:4200`.

El primer test versionado es `frontend/e2e/app-startup.spec.ts`, que valida que la pantalla `/login` carga y muestra los controles basicos de acceso.

## Criterio de cierre por fase

Cada fase debe quedar registrada en el Documento 31 y en `docs/Docs_Asistentes`, indicando comandos ejecutados y resultado.

Si una prueba no se ejecuta, debe documentarse el motivo.
