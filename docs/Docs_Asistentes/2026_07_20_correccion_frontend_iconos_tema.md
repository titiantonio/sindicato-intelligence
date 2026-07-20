# Correccion frontend de iconos y tema

Fecha: 2026-07-20

## Objetivo

Corregir la visualizacion de iconos en tarjetas metricas y menus, y restaurar el funcionamiento fiable del cambio a tema oscuro en el backoffice Angular.

## Contexto

- Fase Documento 30: Fase 11 Frontend Angular.
- Tarea Documento 31: mantenimiento correctivo posterior al Sprint 11, registrado como `19.41 Correccion visual frontend iconos y tema`.
- La incidencia aparece al acceder al frontend Docker/Angular: clases `pi pi-*` sin iconos visibles y cambio de tema no fiable.

## Archivos modificados

- `frontend/angular.json`.
- `frontend/src/app/app.ts`.
- `frontend/src/app/app.spec.ts`.
- `frontend/src/app/core/services/theme.service.ts`.
- `frontend/src/app/core/services/theme.service.spec.ts`.
- `frontend/src/app/layout/shell/shell.component.ts`.
- `frontend/src/app/layout/shell/shell.component.html`.
- `frontend/src/app/layout/shell/shell.component.scss`.
- `frontend/src/app/shared/components/metric-card/metric-card.component.ts`.
- `frontend/src/app/shared/components/metric-card/metric-card.component.html`.
- `frontend/src/app/shared/components/metric-card/metric-card.component.scss`.
- `frontend/src/app/shared/components/metric-card/metric-card.component.spec.ts`.
- `frontend/src/styles.scss`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_07_20_correccion_frontend_iconos_tema.md`.

## Decisiones

- No introducir nuevas dependencias: `primeicons` ya estaba instalado.
- Incluir `node_modules/primeicons/primeicons.css` en los estilos globales de build y test para que las clases `pi pi-*` usadas por PrimeNG tengan fuente y reglas CSS.
- Desactivar `inlineCritical` en el build Angular de produccion. La CSP de Nginx bloqueaba el `onload` inline generado por Angular para cambiar el stylesheet de `media=print` a `all`; como consecuencia solo quedaba aplicada la CSS critica clara y fallaban iconos/tema en Docker.
- Inicializar `ThemeService` desde `App` para que `data-theme` se aplique al documento desde el bootstrap, no solo cuando se instancia el shell autenticado.
- Aplicar `data-theme` y clases `theme-light`/`theme-dark` tambien en `body` para reforzar la herencia de tokens visuales claro/oscuro y la integracion PrimeNG.
- Definir `.metric-grid` en `settings-page.component.scss` porque el grid de dashboard no aplica fuera de su componente por encapsulacion Angular.
- Subir la version frontend a `0.0.25`.

## Pruebas y verificaciones

- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/app.spec.ts --include=src/app/core/services/theme.service.spec.ts --include=src/app/layout/shell/shell.component.spec.ts --include=src/app/shared/components/metric-card/metric-card.component.spec.ts` OK, 8 tests.
- Frontend build: `npm.cmd run build` OK.
- Docker frontend: `docker compose build frontend` y `docker compose up -d frontend` OK.
- Stack TFM: `./tfm-check.ps1` OK tras recrear el frontend.
- Browser real Chrome headless contra `http://localhost:4200`: login correcto, dashboard y settings cargan, CSS completo con `media=null`, tema cambia colores reales, menu con 9 iconos, metric cards con 20 iconos y `/settings` con tarjetas en 2 columnas.

## Notas

- No se modifican contratos API ni logica de negocio.
- No aplica backend ni Flyway.
