# Cierre Sprint 11: merge, scheduling, editor y auditoria

Fecha: 2026-06-13

## Resumen

Se implemento el cierre funcional de Sprint 11 para el MVP: fusion de eventos, editor manual de contenido, programacion automatica de publicaciones y auditoria visible para administradores.

## Backend

- `POST /api/v1/events/merge` con `targetEventId` y `sourceEventIds`.
- `PUT /api/v1/content/{id}` para edicion manual de titulo, contenido y tono.
- `POST /api/v1/publications/{contentId}/schedule` con fecha futura y contenido aprobado.
- `PublicationStatus.SCHEDULED` y columna `publications.scheduled_at`.
- Scheduler configurable `app.publication.scheduler.fixed-delay-ms` para publicar vencidas.
- Tabla `audit_log` y APIs ADMIN `/api/v1/audit/users` y `/api/v1/audit/editorial`.

## Frontend

- Fusion de eventos desde listado.
- Editor manual y programacion desde bandeja de contenido.
- Historial de publicaciones muestra `SCHEDULED` y `scheduledAt`.
- Nueva pantalla ADMIN `/audit` con auditoria de usuarios y editorial.

## Validacion

- `mvn test`: OK, 191 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 53 tests.
- `npm.cmd run build`: OK.

## Versionado

- Backend actualizado a `0.0.40-SNAPSHOT`.