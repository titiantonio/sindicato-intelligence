# Accesibilidad Frontend

Fecha: 2026-06-28

## Objetivo

Registrar el estado de accesibilidad de la modernizacion frontend PrimeNG + Tailwind con objetivo WCAG 2.2 AA.

## Mejoras Aplicadas

- Shell global con enlace `Saltar al contenido principal`.
- `main` identificado como `#main-content` y enfocable.
- Navegacion principal con nombre accesible.
- Selector de tema con `aria-pressed`, `aria-label` y titulo contextual.
- Boton de menu movil con `aria-label`.
- Iconos decorativos marcados con `aria-hidden`.
- Foco visible global con `:focus-visible`.
- Tokens de contraste revisados para tema claro y oscuro.
- `StatusBadge` migrado a `p-tag` de PrimeNG.
- Pantallas de autenticacion migradas a `pInputText`, `pButton` y `p-message`.
- Pantallas de autenticacion con landmark `main` y labels explicitos asociados por `for/id`.
- Pantallas operativas, editoriales y ADMIN migradas parcialmente a `p-message`, `pInputText`, `pButton` y `p-dialog`.
- Tablas operativas migradas a `app-standard-table` sobre PrimeNG `p-table`, con cabeceras, filtros, estados vacios/carga y paginacion homogeneos.
- Cabeceras de tablas operativas con `scope="col"`.
- Filtros y paginacion de tablas migrados a `p-select` de PrimeNG.
- PrimeNG configurado con selector de dark mode coherente con `ThemeService`.

## Criterios para las Pantallas Pendientes

- Todo formulario debe tener label visible o asociacion accesible equivalente.
- Los errores de validacion deben quedar junto al campo y ser comprensibles.
- Las tablas complejas deben usar `app-standard-table`, mantener cabeceras claras, paginacion y lectura por teclado.
- Los dialogos deben tener titulo, cierre accesible y foco contenido.
- No usar ARIA cuando HTML semantico o PrimeNG ya resuelva el caso.
- Mantener contraste AA en badges, botones y estados.

## Pendientes

- Completar formularios complejos con componentes PrimeNG especificos donde aporte accesibilidad adicional.
- Revisar formularios de usuarios, fuentes, settings y contenido con pruebas manuales de teclado.
- Mantener revision manual de teclado en modales y formularios antes de despliegue productivo.
- Incorporar una herramienta automatizada de auditoria si el proyecto decide anadirla.

## Verificacion

- Build frontend verificado con `npm run build`.
- Suite frontend verificada con `npm test -- --watch=false --browsers=ChromeHeadless`: 146 tests OK.
- Revision responsive con navegador en mobile `390x844`, tablet `768x1024` y desktop `1440x900` para rutas auth y backoffice principales: sin overflow horizontal global, con `main`, navegacion nombrada y controles basicos accesibles.
- Tablas HTML operativas sustituidas por `app-standard-table` en plantillas Angular.
- Sin mojibake visible detectado en plantillas Angular tras la pasada final.
- Warning residual de Karma por fuentes PrimeIcons queda documentado como no bloqueante.
