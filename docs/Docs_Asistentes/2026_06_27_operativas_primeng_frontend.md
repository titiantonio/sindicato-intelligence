# Pantallas operativas PrimeNG frontend

## Fecha

2026-06-27

## Objetivo

Continuar la modernizacion PrimeNG + Tailwind en las pantallas operativas principales del backoffice.

## Contexto

Trabajo posterior al Sprint 12 sobre Fase 11 Frontend Angular. No se modifican backend, contratos `/api/v1`, servicios Angular ni reglas de dominio.

## Fase MVP

Fase 11 Frontend Angular, mejora posterior al MVP.

## Archivos modificados

- `frontend/src/app/features/dashboard/dashboard-page.component.*`
- `frontend/src/app/features/events/events-page.component.*`
- `frontend/src/app/features/events/event-detail-page.component.*`
- `frontend/src/app/features/news/news-page.component.*`
- `frontend/src/app/features/news/news-detail-page.component.*`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

## Decisiones

- Se migran mensajes, filtros, botones, acciones y dialogos a PrimeNG.
- Las tablas se mantienen nativas en esta iteracion para conservar filtros, ordenacion y paginacion ya verificadas.
- La migracion completa a `p-table` queda como refinamiento posterior por pantalla.

## Pruebas o verificaciones

- `npm run build`: OK, con warning residual de bundle inicial 509.27 KB sobre budget 500 KB y warnings SCSS historicos.
- Tests focales de `dashboard`, `events`, `event-detail` y `news`: 23 tests, 0 fallos.
