# Accesibilidad Frontend

Fecha: 2026-06-27

## Objetivo

Registrar el estado de accesibilidad de la modernizacion frontend PrimeNG + Tailwind con objetivo WCAG 2.2 AA.

## Mejoras Aplicadas

- Shell global con enlace `Saltar al contenido principal`.
- `main` identificado como `#main-content` y enfocable.
- Selector de tema con `aria-pressed`, `aria-label` y titulo contextual.
- Boton de menu movil con `aria-label`.
- Iconos decorativos marcados con `aria-hidden`.
- Foco visible global con `:focus-visible`.
- Tokens de contraste revisados para tema claro y oscuro.
- `StatusBadge` migrado a `p-tag` de PrimeNG.
- PrimeNG configurado con selector de dark mode coherente con `ThemeService`.

## Criterios para las Pantallas Pendientes

- Todo formulario debe tener label visible o asociacion accesible equivalente.
- Los errores de validacion deben quedar junto al campo y ser comprensibles.
- Las tablas complejas deben mantener cabeceras claras, paginacion y lectura por teclado.
- Los dialogos deben tener titulo, cierre accesible y foco contenido.
- No usar ARIA cuando HTML semantico o PrimeNG ya resuelva el caso.
- Mantener contraste AA en badges, botones y estados.

## Pendientes

- Migrar dialogos propios de cada pantalla a componentes PrimeNG con foco gestionado.
- Revisar formularios de auth, usuarios, fuentes, settings y contenido.
- Revisar tablas de eventos, noticias, auditoria, settings y publicaciones.
- Ejecutar auditoria visual/manual tras cada migracion por pantalla.
- Incorporar una herramienta automatizada de auditoria si el proyecto decide anadirla.

## Verificacion

- Build frontend verificado con `npm run build`.
- Suite frontend verificada con `npm test -- --watch=false --browsers=ChromeHeadless`: 146 tests OK.
- Queda pendiente la revision manual WCAG 2.2 AA por pantalla durante la migracion visual detallada.
