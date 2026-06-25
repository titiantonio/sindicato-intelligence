# Fecha

2026-06-25

# Objetivo

Corregir desbordes visuales en la pantalla de eventos, especialmente en la fusion de eventos y la columna de acciones, y unificar las confirmaciones eliminando dialogos nativos de navegador.

# Contexto

La intervencion corresponde al Sprint 11 Frontend Angular como mejora correctiva posterior al Sprint 12. No cambia contratos REST ni reglas de dominio; solo ajusta experiencia de usuario del backoffice.

# Fase MVP

- Documento 30: Fase 11 Frontend Angular.
- Documento 31: registrada la seccion `16.43 Ajuste UX pantalla eventos y confirmaciones - 2026-06-25`.

# Archivos modificados

- `frontend/src/app/features/events/events-page.component.ts`
- `frontend/src/app/features/events/events-page.component.html`
- `frontend/src/app/features/events/events-page.component.scss`
- `frontend/src/app/features/events/events-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se reemplaza el dropdown nativo de evento destino por una lista compacta con radio para evitar popups del sistema con ancho condicionado por titulos largos.
- Las opciones de fusion usan truncado, scroll interno y resumen operativo para impacto, numero de noticias y seleccion actual.
- Las acciones de la tabla pasan a un grid interno para evitar deformar la columna.
- Las confirmaciones de fusion y descarte usan un modal visual propio, coherente con otros modales del backoffice.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts`: 8 tests, 0 fallos.
- `npm.cmd run build`: OK. Quedan avisos de budgets de Angular, incluido `events-page.component.scss` por superar el limite configurado de 4 KB.
