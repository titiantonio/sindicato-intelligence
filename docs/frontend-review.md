# Frontend Review

Fecha: 2026-06-27

## Estado

Modernizacion tecnica iniciada y verificada:

- Angular actualizado a 21.
- PrimeNG y `@primeng/themes` instalados y configurados.
- Tailwind CSS 4 y `tailwindcss-primeui` configurados.
- PrimeIcons disponible globalmente.
- Shell global modernizado.
- Tokens globales ampliados.
- Componentes compartidos `StatusBadge` y `MetricCard` adaptados.
- Build frontend correcto.

## Riesgos

- El bundle inicial supera el budget tras incorporar PrimeNG/Tailwind.
- Persisten warnings de presupuesto SCSS en pantallas con estilos historicos extensos.
- La migracion visual de todas las plantillas debe hacerse por bloques para no romper flujos editoriales.
- No se ha ejecutado aun una auditoria manual completa de responsive y accesibilidad por pantalla.

## Prioridad Alta

- Migrar auth y shell completo a patrones PrimeNG/Tailwind.
- Migrar eventos y detalle de evento, por ser la pantalla central del producto.
- Migrar tablas principales a PrimeNG `p-table` con paginacion, filtros y estados.
- Reducir SCSS duplicado de `events`, `users`, `sources`, `audit` y `settings`.

## Prioridad Media

- Migrar contenido y publicaciones.
- Migrar settings con tabs y tablas PrimeNG.
- Normalizar empty/loading/error states.
- Revisar responsive mobile/tablet para formularios y tablas.

## Prioridad Baja

- Ajustar microinteracciones y animaciones.
- Evaluar lazy loading de rutas para reducir bundle inicial.
- Revisar budgets tras la migracion completa.

## Verificacion Ejecutada

- `npm install`: OK.
- `npm run build`: OK.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests.

## Verificacion Pendiente

- Tests focales por pantalla tras cada migracion.
- Revision responsive manual.
- Revision WCAG 2.2 AA por pantalla.
