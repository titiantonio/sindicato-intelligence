# Fecha

2026-06-26

# Objetivo

Reducir los tiempos de espera percibidos en `dashboard`, `events` y `/settings`, evitando pantallas vacias con mensajes de carga y optimizando lecturas backend pesadas.

# Contexto

Mantenimiento correctivo posterior a Sprint 11 y Sprint 12. Las pantallas afectadas ya pertenecen al backoffice operativo y a la configuracion ADMIN. No se modifica la secuencia del MVP ni se cambia el contrato REST publico.

# Fase MVP

Fase 11 Frontend Angular y Fase 12 Optimizacion, automatizaciones internas, observabilidad y configuracion ADMIN.

# Archivos modificados

- Backend dashboard: `DashboardSnapshotUseCase`, `DashboardSnapshotQueryRepository`, `JpaDashboardSnapshotQueryRepository` y `DashboardController`.
- Backend eventos: `ListEventsUseCase`, `EventSummaryQueryRepository`, `EventSummaryView`, `JpaEventSummaryQueryRepository` y `EventController`.
- Base de datos: `V16__performance_indexes_backoffice.sql`.
- Frontend: componentes de dashboard, eventos y settings.
- Tests: `DashboardControllerTest`, `EventControllerTest` y `SettingsPageComponent`.
- Versionado y documentacion: `backend/pom.xml`, `CHANGELOG.md` y Documento 31.

# Decisiones

- Mantener sin cambios las URLs y el JSON de `GET /api/v1/dashboard`, `GET /api/v1/events`, `/api/v1/automation/**`, `/api/v1/ai/**` y `GET /api/v1/settings/telegram`.
- Mover lecturas de resumen a consultas optimizadas de infraestructura, expuestas mediante puertos internos de aplicacion.
- Mantener reglas de visibilidad y estado editorial en backend.
- Cargar `/settings` por pestana: metricas al inicio; prompts, automatizaciones/proveedores IA y Telegram bajo demanda.
- Mostrar skeletons en dashboard/eventos para que la interfaz pinte estructura inmediata mientras llegan los datos.

# Pruebas o verificaciones

- `backend`: `mvnw.cmd "-Dtest=DashboardControllerTest,EventControllerTest" test` OK, 10 tests.
- `frontend`: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/settings/settings-page.component.spec.ts` OK, 26 tests.
- `frontend`: `npm.cmd run build` OK antes del cierre documental. Mantiene warnings de presupuesto, incluido `events-page.component.scss`.

