# Frontend Review

Fecha: 2026-06-28

## Estado

Modernizacion tecnica iniciada y verificada:

- Angular actualizado a 21.
- PrimeNG y `@primeng/themes` instalados y configurados.
- Tailwind CSS 4 y `tailwindcss-primeui` configurados.
- PrimeIcons disponible globalmente.
- Shell global modernizado.
- Tokens globales ampliados.
- Componentes compartidos `StatusBadge` y `MetricCard` adaptados.
- Pantallas auth migradas a controles PrimeNG.
- Pantallas operativas `dashboard`, `events`, `event-detail`, `news` y `news-detail` migradas en mensajes, filtros, botones, acciones y dialogos PrimeNG.
- Pantallas editoriales/admin `content`, `publications`, `sources` y `audit` migradas parcialmente a mensajes, filtros, botones, acciones y dialogos PrimeNG.
- Pantallas ADMIN/detalle `users`, `settings`, `content-detail` y `publication-detail` migradas parcialmente a mensajes, filtros, botones, acciones y dialogos PrimeNG.
- Tablas operativas unificadas con `app-standard-table` sobre PrimeNG `p-table` en `dashboard`, `events`, `event-detail`, `news`, `content`, `sources`, `users`, `audit` y `settings`.
- Filtros y paginacion de tablas migrados a `p-select` de PrimeNG; los selects restantes pertenecen a formularios existentes.
- Revision responsive integral aplicada sobre el shell, paneles y tablas para evitar crecimiento horizontal de pagina en mobile, tablet, desktop y ultrawide.
- Metric cards revisadas para altura homogenea, grid consistente en dashboard/settings, colores por tokens y datos sin corte. La tarjeta de errores de settings queda en `danger`; publicaciones queda en `secondary` para no comunicar fallo salvo en el dato interno de fallidas.
- Iconos PrimeIcons invalidos sustituidos en navegacion de fuentes y fusion de eventos.
- Accesibilidad base reforzada con landmarks en auth, labels explicitos en formularios de password, `aria-label` en navegacion principal y `scope="col"` en cabeceras de tablas.
- Rutas de pantallas cargadas con `loadComponent`.
- Auditoria npm sin vulnerabilidades tras overrides controlados.
- Build frontend correcto y budgets recalibrados para la nueva base PrimeNG + Tailwind.

## Riesgos

- El bundle inicial queda en 527.10 KB tras incorporar `p-table` comun; el warning se evita con budget inicial de 535 KB y error budget intacto en 1 MB.
- Los estilos historicos de tablas HTML se han retirado de pantallas migradas; todos los SCSS de componente quedan por debajo del warning budget de 6 KB.
- Persiste warning no bloqueante de Karma por fuentes PrimeIcons en `/base/media/*`; no afecta tests ni build.
- La revision responsive con navegador queda ejecutada en mobile, tablet y desktop para rutas principales; quedan como revision previa a despliegue los flujos interactivos profundos de modales/formularios.
- La revision responsive integral de 2026-06-30 valida tambien ultrawide y detalles reales disponibles; las tablas densas mantienen scroll interno controlado por `app-standard-table`.

## Prioridad Alta

- Mantener documentado el warning de PrimeIcons en Karma hasta que Angular/Karma permita mapear `/base/media/*` sin configuracion intrusiva.

## Prioridad Media

- Migrar selects de formularios complejos a componentes PrimeNG especificos en una tarea de formularios, no mezclada con tablas.
- Normalizar empty/loading/error states.
- Revisar manualmente modales y formularios complejos con teclado antes de despliegue productivo.

## Prioridad Baja

- Ajustar microinteracciones y animaciones.
- Evaluar lazy loading de rutas para reducir bundle inicial.
- Revisar budgets tras la migracion completa.

## Verificacion Ejecutada

- `npm install`: OK.
- `npm run build`: OK.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- Tests focales `content`, `publications`, `sources` y `audit`: OK, 21 tests.
- Tests focales `users`: OK, 14 tests incluyendo formatter de publicaciones.
- Tests focales `settings`: OK, 12 tests.
- Build tras cierre de tablas unificadas: OK sin warnings de budget. Bundle inicial: 527.10 KB.
- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin tablas HTML operativas restantes.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests tras `p-select` y limpieza SCSS.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- Revision navegador mobile/tablet/desktop: OK en rutas auth y backoffice principales, sin overflow horizontal global y sin controles basicos sin nombre accesible detectados.
- Revision Chrome headless por CDP mobile/tablet/desktop/ultrawide: OK en 60 combinaciones ruta/viewport, sin overflow horizontal de documento, sin tablas fuera del viewport y sin iconos visibles con tamano cero.
- Build final tras revision responsive: `npm run build` OK sin warnings de budget. Bundle inicial: 527.32 KB.
- Suite final tras revision responsive: `npm test -- --watch=false --browsers=ChromeHeadless` OK, 146 tests; persiste warning no bloqueante de fuentes PrimeIcons en Karma.
- Ajuste metric cards: `npm run build` OK, tests focales frontend OK, 20 tests, y test focal backend de metricas de dashboard OK. `Publicaciones` queda con tono `secondary` y `Errores` con tono `danger`.

## Verificacion Pendiente

- Revision manual profunda de teclado en modales y formularios complejos antes de despliegue productivo.
