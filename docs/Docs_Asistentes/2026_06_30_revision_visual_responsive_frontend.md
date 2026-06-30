# Revision visual responsive frontend

## Fecha

2026-06-30

## Objetivo

Cerrar la revision visual integral del frontend PrimeNG + Tailwind para evitar scroll horizontal de pagina, paginas mas anchas que la pantalla e iconos PrimeIcons invisibles.

## Contexto

Fase MVP correspondiente: Fase 11 Frontend Angular, como cierre posterior al roadmap de modernizacion frontend y a la tarea 19.12 de tablas unificadas.

La revision se registra como tarea 19.13 en el Documento 31.

## Archivos modificados

- `frontend/src/app/shared/components/standard-table/standard-table.component.scss`
- `frontend/src/app/layout/shell/shell.component.scss`
- `frontend/src/app/layout/shell/shell.component.ts`
- `frontend/src/styles.scss`
- Plantillas de tablas en `dashboard`, `news`, `events`, `event-detail`, `content`, `sources`, `users`, `audit` y `settings`
- SCSS de `settings`, `users` y `sources`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/design-system.md`
- `docs/frontend-review.md`
- `docs/accessibility.md`
- `CHANGELOG.md`

## Decisiones

- Mantener `app-standard-table` como unico punto de control del scroll horizontal de tablas.
- Permitir scroll interno en tablas densas, pero no crecimiento horizontal del documento ni de paneles padres.
- Sustituir iconos inexistentes de PrimeIcons por iconos disponibles: `pi-database` para fuentes y `pi-sitemap` para fusion de eventos.
- Mantener intactos backend, servicios Angular, rutas, guards, roles y contratos `/api/v1`.

## Pruebas o verificaciones

- `rg "pi-rss|pi-object-group" frontend/src/app`: sin resultados.
- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin resultados.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons y cierre lento de ChromeHeadless.
- `npm run build`: OK.
- Revision Chrome headless por CDP en mobile `390x844`, tablet `768x1024`, desktop `1440x900` y ultrawide `1920x1080`: 60 combinaciones ruta/viewport sin overflow horizontal de documento, sin tablas fuera del viewport y sin iconos visibles con tamano cero.
