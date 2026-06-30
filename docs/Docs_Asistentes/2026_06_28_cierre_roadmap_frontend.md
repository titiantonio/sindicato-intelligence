# 2026-06-28 - Cierre roadmap frontend con tablas unificadas

## Objetivo

Cerrar el roadmap de modernizacion frontend PrimeNG + Tailwind con una tabla operativa comun para todo el backoffice.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular y bloque posterior a Sprint 12.
- Backlog operativo: Documento 31, tarea 19.12.
- Se mantiene intacto backend, contratos `/api/v1`, JWT, roles, guards, servicios Angular y reglas de negocio.

## Archivos modificados

- `frontend/src/app/shared/components/standard-table/*`
- Plantillas y componentes Angular de `dashboard`, `events`, `event-detail`, `news`, `content`, `sources`, `users`, `audit` y `settings`.
- Plantillas auth de `login`, `forgot-password`, `reset-password` y `change-password`.
- `frontend/src/app/layout/shell/shell.component.html`
- `frontend/angular.json`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/design-system.md`
- `docs/frontend-review.md`
- `docs/accessibility.md`
- `CHANGELOG.md`

## Decisiones tomadas

- Crear `app-standard-table` como wrapper unico sobre PrimeNG `p-table`.
- Mantener la logica existente de filtros, ordenacion y paginacion en cada pantalla para evitar regresiones.
- Pasar al componente compartido filas ya preparadas por cada pantalla.
- Conservar la paginacion backend de `/news` sin cambiar API.
- Recalibrar el warning budget inicial de `525kB` a `535kB` por el coste comun de `p-table`, manteniendo el error budget en `1MB`.
- Documentar el warning residual de Karma por fuentes PrimeIcons como deuda no bloqueante.
- Revisar tareas abiertas del Documento 31 y cerrar las que ya estaban implementadas.
- Migrar filtros y paginacion de tablas a `p-select`, manteniendo nativos los selects de formularios existentes.
- Retirar SCSS heredado de tablas HTML y paginaciones antiguas tras la migracion a `app-standard-table`.
- Reforzar accesibilidad base con landmarks en auth, labels explicitos de formularios, `scope="col"` en cabeceras de tablas y nombre accesible en navegacion principal.
- Mantener el warning de PrimeIcons en Karma como deuda no bloqueante documentada tras probar una configuracion de assets que no resolvio el 404.

## Verificacion

- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin tablas HTML operativas restantes.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.
- `npm run build`: OK sin warnings de budget. Bundle inicial: `527.10 kB`.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos.
- `rg "Ã|Â|�|<table|</table>" frontend/src/app`: sin mojibake visible ni tablas HTML operativas.

- Revision con navegador en mobile `390x844`, tablet `768x1024` y desktop `1440x900`: rutas auth y backoffice principales con `main`, sin overflow horizontal global, nav nombrada, cabeceras con `scope` y sin controles basicos sin nombre accesible.

## Pendiente

- Revision manual profunda de teclado en modales y formularios complejos antes de despliegue productivo.
- Warning no bloqueante de Karma por fuentes PrimeIcons en `/base/media/*`; se probo configuracion de assets desde `node_modules/primeicons/fonts` y no resolvio el warning del runner.
