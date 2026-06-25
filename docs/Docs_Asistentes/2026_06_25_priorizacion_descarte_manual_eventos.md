# Fecha

2026-06-25

# Objetivo

Priorizar la tabla de eventos y la fusion por impacto y numero de noticias, mostrar informacion editorial mas util y permitir descartar eventos manualmente desde el backoffice.

# Contexto

La intervencion corresponde a la Fase 7 Eventos y al Sprint 11 Frontend Angular, como mejora correctiva posterior al cierre del Sprint 12. Se mantiene `Event` como entidad central y se conserva la logica de negocio en Spring Boot.

# Fase MVP

- Documento 30: Fase 7 Eventos.
- Documento 30: Fase 11 Frontend Angular.
- Documento 31: registrada la seccion `16.42 Priorizacion y descarte manual de eventos - 2026-06-25`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/ListEventsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/DiscardEventUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/api/EventController.java`
- `backend/src/main/java/es/sindicato/intelligence/audit/application/AuditDetailFormatter.java`
- `backend/src/test/java/es/sindicato/intelligence/event/application/DiscardEventUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/event/api/EventControllerTest.java`
- `frontend/src/app/core/services/event.service.ts`
- `frontend/src/app/core/services/event.service.spec.ts`
- `frontend/src/app/features/events/events-page.component.ts`
- `frontend/src/app/features/events/events-page.component.html`
- `frontend/src/app/features/events/events-page.component.scss`
- `frontend/src/app/features/events/events-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- El orden editorial de eventos se aplica en backend y se replica como orden inicial en Angular: impacto, numero de noticias, ultima actualizacion e id.
- El descarte manual archiva el evento mediante un caso de uso de aplicacion y queda auditado como `EVENT_DISCARDED`.
- No se eliminan noticias ni asociaciones; el evento archivado deja de ser visible por la politica existente de visibilidad operativa.
- La fusion de eventos muestra impacto, volumen de noticias y descripcion para facilitar decisiones editoriales.

# Pruebas o verificaciones

- Backend focal: `mvn "-Dtest=DiscardEventUseCaseTest,MergeEventsUseCaseTest,EventControllerTest" test`: 10 tests, 0 fallos, 0 errores.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts`: 7 tests, 0 fallos.
- Primer intento paralelo backend/frontend supero el timeout local de 120 segundos sin resultado util; se repitio de forma separada con timeout ampliado.
