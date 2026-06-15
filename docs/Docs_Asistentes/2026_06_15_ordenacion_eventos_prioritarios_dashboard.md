# 2026-06-15 - Ordenacion de eventos prioritarios del dashboard

## Objetivo

Ajustar la tabla de eventos prioritarios del dashboard para priorizar primero los eventos de mayor impacto y despues los que tienen mas noticias asociadas.

## Contexto

- Fase MVP: Fase 11, Frontend Angular / backoffice.
- Backlog operativo: Documento 31, mejora correctiva de Sprint 11.
- Alcance acordado: ordenacion por prioridad editorial y filtros select para impacto y estado.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/dashboard/application/DashboardSnapshotUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `frontend/src/app/features/dashboard/dashboard-page.component.ts`
- `frontend/src/app/features/dashboard/dashboard-page.component.html`
- `frontend/src/app/features/dashboard/dashboard-page.component.scss`
- `frontend/src/app/features/dashboard/dashboard-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se mantiene sin cambios el contrato de `GET /api/v1/dashboard`.
- El backend ordena `priorityEvents` por impacto, numero de noticias asociadas y ultima actualizacion.
- Angular usa ranking semantico para `importance`, evitando orden alfabetico.
- Los filtros de impacto y estado del dashboard usan selectores para reducir errores de filtrado.
- Se incrementa la version backend a `0.0.51-SNAPSHOT`.

## Pruebas y verificaciones

- `mvnw.cmd "-Dtest=DashboardControllerTest" test`: OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/dashboard/dashboard-page.component.spec.ts`: OK, 5 tests.
- `npm.cmd run build`: OK, con warnings no bloqueantes de presupuesto en bundle inicial, `users-page.component.scss` y `sources-page.component.scss`.
- Verificacion visual local en `http://localhost:4200/dashboard`: OK, la tabla renderiza selectores de cabecera para impacto y estado.
