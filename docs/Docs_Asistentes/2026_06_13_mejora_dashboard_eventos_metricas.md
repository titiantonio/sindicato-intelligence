# Fecha

2026-06-13

# Objetivo

Mejorar el dashboard y la tabla de eventos para mostrar metricas comparativas por dia, reducir ruido en eventos prioritarios y permitir busqueda, filtrado y ordenacion en la pantalla `/events`.

# Contexto

Trabajo de mejora sobre Fase 11 / Sprint 11. El dashboard ya consumia API real, pero sus tarjetas mostraban totales historicos y la tabla de eventos priorizaba eventos activos recientes sin distinguir impacto alto ni ruido. La pantalla de eventos no tenia filtros reales ni ordenacion por columnas.

# Fase MVP

Fase 11: Frontend Angular, con ajuste backend en `GET /api/v1/dashboard`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/dashboard/application/DashboardSnapshotUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/DashboardController.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/MetricCardResponse.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `frontend/src/app/core/models/dashboard.models.ts`
- `frontend/src/app/shared/components/metric-card/*`
- `frontend/src/app/features/dashboard/*`
- `frontend/src/app/features/events/*`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se usa `Europe/Madrid` para los rangos de hoy y ayer.
- El dashboard prioriza eventos `OPEN` o `MONITORING`, con importancia `HIGH` o `CRITICAL`, excluyendo categoria `OTROS`.
- La tabla del dashboard se limita a 10 eventos y permite navegar al detalle.
- Los filtros y ordenacion avanzados se aplican solo en la pantalla `/events`, en frontend, sin cambiar `GET /api/v1/events`.

# Pruebas o verificaciones

- Backend: `mvnw.cmd "-Dtest=DashboardControllerTest" test` ejecutado correctamente.
- Frontend: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts` ejecutado correctamente.
- Frontend build: `npm.cmd run build` ejecutado correctamente.
