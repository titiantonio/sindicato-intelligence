# Fecha

2026-06-26

# Objetivo

Implementar la diferenciacion editorial de eventos para identificar eventos pendientes, analizados, publicados y descartados manualmente, excluir los ya tratados del dashboard prioritario y permitir generar analisis desde el detalle del evento.

# Contexto

Mejora correctiva posterior a Sprint 12, alineada con las Fases 7, 8, 9, 10 y 11 del Documento 30. La entidad central sigue siendo `Event`; la logica de negocio queda en Spring Boot y Angular consume el contrato API.

# Fase MVP

- Fase 7: Eventos.
- Fase 8: Analisis IA.
- Fase 9: Contenido.
- Fase 10: Publicacion.
- Fase 11: Backoffice Angular.

# Archivos modificados

- Backend eventos: dominio `Event`, persistencia JPA, DTOs/API, descarte manual y restauracion.
- Backend dashboard: calculo y respuesta de eventos prioritarios.
- Backend datos: `V15__event_manual_discard_status.sql`.
- Backend seguridad: autorizacion de `/api/v1/analysis/**` para `ADMIN` y `EDITOR`.
- Frontend eventos: listado, detalle, servicios y modelos.
- Frontend dashboard: modelos y pruebas.
- Documentacion: Documento 31, `CHANGELOG.md` y `backend/pom.xml`.

# Decisiones

- `EventStatus` conserva el ciclo tecnico (`OPEN`, `MONITORING`, `CLOSED`, `ARCHIVED`).
- Se anade `EventEditorialStatus` como estado derivado: `PENDING_ANALYSIS`, `ANALYZED`, `PUBLISHED`, `DISCARDED`.
- El descarte manual ya no archiva el evento; se persiste con `manual_discarded` y se puede revertir.
- Un evento se considera publicado si existe contenido asociado con `ContentStatus.PUBLISHED`.
- Los descartes automaticos por IA siguen ocultos por la politica de visibilidad existente.

# Pruebas o verificaciones

- `mvn -DskipTests compile`: OK.
- `mvn "-Dtest=EventTest,DiscardEventUseCaseTest,RestoreDiscardedEventUseCaseTest" test`: 16 tests, 0 fallos.
- `mvn "-Dtest=EventControllerTest" test`: 7 tests, 0 fallos.
- `mvn "-Dtest=DashboardControllerTest#excludesAnalyzedPublishedAndManuallyDiscardedEventsFromPriorityEvents" test`: 1 test, 0 fallos.
- `npx ng test --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/events/event-detail-page.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts --include=src/app/core/services/analysis.service.spec.ts`: 20 tests, 0 fallos.

La ejecucion completa de `DashboardControllerTest` supero el limite operativo de 240 segundos en esta sesion; se documento el motivo y se verifico de forma focal el escenario nuevo de exclusion de prioritarios.
