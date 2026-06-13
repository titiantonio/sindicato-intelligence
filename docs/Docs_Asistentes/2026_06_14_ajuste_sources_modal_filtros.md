# 2026-06-14 - Ajuste de fuentes con modal y filtros

## Objetivo

Corregir la pantalla `/sources` del backoffice Angular para evitar que el contenido ensanche la pagina, mover el alta/edicion de fuentes a modales y permitir ordenacion y filtrado por los campos de la tabla.

## Contexto

Intervencion alineada con la Fase 11 del Documento 30 y con Sprint 11 Frontend Angular del Documento 31. No se modifican contratos backend ni logica de negocio.

## Archivos modificados

- `frontend/src/app/features/sources/sources-page.component.ts`
- `frontend/src/app/features/sources/sources-page.component.html`
- `frontend/src/app/features/sources/sources-page.component.scss`
- `frontend/src/app/features/sources/sources-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se elimina el formulario fijo de "Nueva fuente" para dejar una unica tabla operativa en la pantalla.
- Se reutiliza el mismo formulario reactivo en un modal para creacion y edicion.
- La ordenacion y los filtros se resuelven en cliente porque el contrato actual `GET /api/v1/sources` no define parametros de busqueda.
- El ancho de pagina se controla con `min-width: 0` y overflow horizontal solo en el wrapper de la tabla.
- Se muestran ID, URL, creacion y actualizacion para que los filtros y la ordenacion cubran todos los campos relevantes de `SourceResponse`.

## Verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/sources/sources-page.component.spec.ts`: OK, 7 tests.
- `npm.cmd run build`: OK.
- Verificacion visual en navegador local:
  - `/sources` carga 54 fuentes reales.
  - Sin overflow horizontal de pagina en escritorio.
  - Sin overflow horizontal de pagina en viewport movil 390x844.
  - Modal de "Anadir fuente" visible.
  - Modal de "Editar fuente" visible con datos precargados.
  - Busqueda global filtra correctamente.

## Tareas Documento 31

- Actualizada la seccion `16.23 Ajuste de gestion de fuentes - 2026-06-14`.
- No se abre Sprint 12 ni se cambian tareas de backend.
