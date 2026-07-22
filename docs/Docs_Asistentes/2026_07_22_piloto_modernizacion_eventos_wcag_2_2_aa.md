# Piloto de modernización visual de eventos y WCAG 2.2 AA

## Fecha

2026-07-22

## Objetivo

Definir una base verificable de accesibilidad WCAG 2.2 AA y crear en `/events` un piloto visual aislado que permita validar la futura modernización integral del frontend antes de extenderla al resto de páginas y diálogos.

## Contexto y fase

- Fase del Documento 30: Fase 11, frontend Angular, con continuidad de calidad en el Sprint 14 propuesto.
- Backlog: Documento 31, Sprint 14, tareas T14.1 y T14.2.
- Se seleccionó `/events` porque `Event` es el agregado principal y la ruta reúne métricas, búsqueda, filtros, tabla, estados, paginación, acciones, selección múltiple y diálogo.
- La intervención no declara conformidad global: las rutas restantes y la revisión manual final continúan pendientes.

## Archivos modificados

- `docs/accessibility.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `CHANGELOG.md`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `frontend/playwright.config.ts`.
- `frontend/e2e/smoke.mock.spec.ts`.
- `frontend/e2e/events-pilot.mock.spec.ts`.
- `frontend/src/app/features/events/events-page.component.html`.
- `frontend/src/app/features/events/events-page.component.ts`.
- `frontend/src/app/features/events/events-page.component.scss`.
- `frontend/src/app/features/events/events-workspace.component.scss`.
- `frontend/src/app/features/events/events-merge.component.scss`.
- `frontend/src/app/features/events/events-page.component.spec.ts`.
- `frontend/src/app/shared/components/standard-table/standard-table.component.html`.
- `frontend/src/app/shared/components/standard-table/standard-table.component.ts`.
- `frontend/src/app/shared/components/standard-table/standard-table.component.scss`.

## Decisiones tomadas

- Mantener el piloto visual limitado a `/events` mediante una variante opt-in de la tabla compartida.
- Usar una cabecera de contexto con métricas operativas, seguida de una superficie de trabajo clara.
- Mantener tema claro y oscuro con tokens locales de contraste comprobable.
- Ampliar la búsqueda global a la descripción de los eventos.
- Exponer ordenación mediante `aria-sort` y nombres accesibles que anuncian la siguiente dirección.
- Convertir la fusión de eventos en una herramienta progresiva con `details`, `fieldset`, `legend`, resumen anunciado y confirmación segura.
- Mantener la columna de acciones visible durante el scroll local de la tabla.
- Usar acciones destructivas compactas con nombre accesible y ayuda contextual para evitar truncados.
- No añadir nuevas dependencias de frontend.

## Accesibilidad aplicada

- Objetivo WCAG 2.2 AA documentado sin sobredeclarar certificación.
- Contraste mínimo calculado para texto, acentos y bordes de controles en ambos temas.
- Reflow verificado a 390 y 320 CSS px sin overflow horizontal global.
- Objetivos táctiles visibles de al menos 24 CSS px.
- Flujo principal y fusión operables con teclado.
- Diálogo con nombre accesible, foco inicial seguro, contención del foco y cierre con `Escape`.
- Paginación expuesta como región de navegación y cambios de página anunciables.
- Nombres accesibles específicos para acciones repetidas de fila.

## Pruebas y verificaciones

- `npx tsc -p tsconfig.app.json --noEmit`: correcto.
- Tests Angular focales de eventos: 10 correctos.
- Suite Angular completa: 158 correctos.
- Suite Playwright mockeada completa: 9 correctos.
- Suite Playwright del piloto tras el ajuste responsive final: 3 correctos.
- `npm run build`: correcto, sin advertencias de presupuesto.
- Revisión visual: escritorio claro, escritorio oscuro y móvil.
- Contrastes principales calculados entre `3.73:1` y `17.06:1`, según elemento y tema.

## Evidencias visuales

- `docs/Docs_Asistentes/2026_07_22_piloto_eventos_claro.png`.
- `docs/Docs_Asistentes/2026_07_22_piloto_eventos_oscuro.png`.
- `docs/Docs_Asistentes/2026_07_22_piloto_eventos_movil.png`.

## Backlog actualizado

- T14.1 completada: especificación de accesibilidad.
- T14.2.1 a T14.2.7 completadas: selección, implementación y verificación técnica del piloto.
- T14.2.8 pendiente: validación visual del usuario.
- T14.3 a T14.7 pendientes: consolidación del sistema visual, extensión a todas las rutas y auditoría final.

## Versionado

- Frontend actualizado de `0.0.36` a `0.0.37`.
- Cambios registrados en `CHANGELOG.md` bajo `Unreleased`.
