# Fecha

2026-06-15

# Objetivo

Corregir el error de arranque del backend producido por el scheduler de publicaciones programadas cuando existen publicaciones `SCHEDULED` vencidas y no hay proveedor `TELEGRAM` registrado.

# Contexto

El log mostraba `TaskUtils$LoggingErrorHandler - Unexpected error occurred in scheduled task` tras arrancar `IntelligenceApplication`. La causa real era `publication provider not found for channel: TELEGRAM` en `PublishScheduledPublicationsUseCase`, porque `TelegramPublisher` solo se registra si `app.publication.telegram.enabled=true`.

# Fase MVP

Mantenimiento correctivo sobre Sprint 11 / Fase 10, funcionalidad de publicaciones programadas.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishScheduledPublicationsUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/application/PublishScheduledPublicationsUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- La correccion queda en application layer, manteniendo la logica de negocio en Spring Boot.
- Una publicacion programada vencida sin proveedor disponible se marca como `FAILED` con payload de error controlado.
- El scheduler deja de propagar la excepcion a Spring Scheduling y evita repetir el mismo fallo en cada ciclo.
- No se modifican migraciones, contratos REST ni configuracion de seguridad.

# Pruebas o verificaciones

- `mvn "-Dtest=PublishScheduledPublicationsUseCaseTest" test`: OK, 3 tests, 0 fallos, 0 errores.
