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
- `frontend/src/app/shared/components/metric-card/metric-card.component.scss`
- `frontend/src/app/features/dashboard/dashboard-page.component.scss`
- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `frontend/src/app/layout/shell/shell.component.scss`
- `frontend/src/app/layout/shell/shell.component.ts`
- `frontend/src/styles.scss`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/DashboardController.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `backend/pom.xml`
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
- Reforzar `app-metric-card` como patron unico de tarjetas metricas, con altura homogenea, colores por tokens y wrapping de datos.
- Unificar la disposicion de datos de metric cards colocando el icono encima del valor en dashboard y settings.
- Ajustar semantica de color: `Errores` siempre en rojo y `Publicaciones` en tono secundario, dejando rojo solo para fallos internos.
- Mantener intactos backend, servicios Angular, rutas, guards, roles y contratos `/api/v1`.

## Pruebas o verificaciones

- `rg "pi-rss|pi-object-group" frontend/src/app`: sin resultados.
- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin resultados.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons y cierre lento de ChromeHeadless.
- `npm run build`: OK.
- Revision Chrome headless por CDP en mobile `390x844`, tablet `768x1024`, desktop `1440x900` y ultrawide `1920x1080`: 60 combinaciones ruta/viewport sin overflow horizontal de documento, sin tablas fuera del viewport y sin iconos visibles con tamano cero.
- Ajuste metric cards: `npm run build` en `frontend/` OK con version `0.0.11`.
- Ajuste metric cards: tests focales frontend `MetricCardComponent`, `DashboardPageComponent` y `SettingsPageComponent` OK, 20 tests, 0 fallos.
- Ajuste metric cards: `mvn -Dtest=DashboardControllerTest#returnsDashboardSnapshotWithDailyMetricsAndPriorityEvents test` OK, validando `Publicaciones` con tono `secondary`.
- Verificacion backend completa focal `mvn -Dtest=DashboardControllerTest test`: no se usa como cierre de esta iteracion porque falla un test de ordenacion de eventos prioritarios por datos reales locales preexistentes en PostgreSQL; la respuesta confirma `Publicaciones` como `secondary` y el metodo de metricas pasa aislado.
