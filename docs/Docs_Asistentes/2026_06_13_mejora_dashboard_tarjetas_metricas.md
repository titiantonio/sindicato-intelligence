# Fecha

2026-06-13

# Objetivo

Redisenar las tarjetas metricas del dashboard con estructura visual avanzada, tres indicadores internos por tarjeta y datos reales del dominio.

# Contexto

Trabajo de mejora sobre Fase 11 / Sprint 11. El dashboard ya consumia API real y mostraba metricas comparativas simples. La nueva necesidad era acercar las tarjetas al diseno de referencia sin introducir dependencias nuevas ni simular valores.

# Fase MVP

Fase 11: Frontend Angular, con ampliacion compatible del contrato `GET /api/v1/dashboard`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/dashboard/application/DashboardSnapshotUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/DashboardController.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/MetricCardResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/MetricItemResponse.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `frontend/src/app/core/models/dashboard.models.ts`
- `frontend/src/app/shared/components/metric-card/*`
- `frontend/src/app/features/dashboard/*`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

# Decisiones

- Se mantiene compatibilidad temporal con `todayValue`, `yesterdayValue` y `difference`.
- Cada tarjeta incorpora `items` con tres indicadores internos calculados en backend.
- Los iconos se implementan como SVG inline en Angular, sin nuevas dependencias.
- El diseno se adapta al tema claro/oscuro actual mediante tokens CSS existentes.
- No se crea un nuevo estado `EN_REVISION`; se usan estados reales existentes del dominio.

# Pruebas o verificaciones

- Backend: `mvnw.cmd "-Dtest=DashboardControllerTest" test` ejecutado correctamente.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` ejecutado correctamente.
- Frontend build: `npm.cmd run build` ejecutado correctamente.
