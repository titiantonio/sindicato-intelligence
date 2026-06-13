# Fecha

2026-06-14

# Objetivo

Corregir el layout de las tarjetas metricas para evitar cortes de valores largos, calcular la ultima actualizacion por tarjeta y estabilizar el menu lateral del backoffice con modo colapsado.

# Contexto

Trabajo de mejora sobre Fase 11 / Sprint 11. El dashboard ya mostraba tarjetas metricas enriquecidas, pero los valores largos podian partirse en varias lineas y todas las tarjetas compartian la misma fecha global de actualizacion. El menu lateral tenia ancho fijo amplio y podia desconfigurarse al acceder a pantallas con tablas anchas.

# Fase MVP

Fase 11: Frontend Angular, con ajuste backend en `GET /api/v1/dashboard`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/dashboard/application/DashboardSnapshotUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/api/DashboardController.java`
- `backend/src/test/java/es/sindicato/intelligence/dashboard/api/DashboardControllerTest.java`
- `frontend/src/app/shared/components/metric-card/*`
- `frontend/src/app/layout/shell/*`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

# Decisiones

- Cada tarjeta calcula su propia `lastUpdatedAt` segun los datos reales que la alimentan.
- Las metricas usan `white-space: nowrap` y `font-size: clamp(...)` para mantener valores largos en una linea.
- El sidebar queda expandido por defecto y el modo colapsado no se persiste.
- En movil se mantiene el comportamiento drawer existente.
- No se anaden dependencias frontend nuevas.

# Pruebas o verificaciones

- Backend: `mvnw.cmd "-Dtest=DashboardControllerTest" test` ejecutado correctamente.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/layout/shell/shell.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` ejecutado correctamente.
- Frontend build: `npm.cmd run build` ejecutado correctamente.
