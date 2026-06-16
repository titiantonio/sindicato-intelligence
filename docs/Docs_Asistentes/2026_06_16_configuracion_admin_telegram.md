# 2026-06-16 - Configuracion ADMIN de Telegram

## Objetivo

Permitir que el administrador configure los parametros necesarios de Telegram desde la misma pagina donde se configuran las automatizaciones internas migradas desde n8n.

## Contexto

- Sprint afectado: Sprint 12 extendido, tarea `T12.18`.
- `WF-06` reside en Spring Boot y publica contenido aprobado.
- Antes de este cambio, Telegram dependia de propiedades de entorno/runtime y el bean `TelegramPublisher` se creaba solo con `app.publication.telegram.enabled=true`.

## Archivos modificados

- Backend:
  - `backend/src/main/resources/db/migration/V8__telegram_publication_settings.sql`
  - `backend/src/main/java/es/sindicato/intelligence/publication/domain/TelegramPublicationSettings.java`
  - `backend/src/main/java/es/sindicato/intelligence/publication/domain/TelegramPublicationSettingsRepository.java`
  - `backend/src/main/java/es/sindicato/intelligence/publication/application/*TelegramPublicationSettings*`
  - `backend/src/main/java/es/sindicato/intelligence/publication/api/TelegramPublicationSettingsController.java`
  - `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/TelegramPublisher.java`
  - `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
  - tests backend de publication.
- Frontend:
  - `frontend/src/app/core/models/application-settings.models.ts`
  - `frontend/src/app/core/services/application-settings.service.ts`
  - `frontend/src/app/features/automation-settings/*`
- Documentacion:
  - `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`
  - `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
  - `CHANGELOG.md`

## Decisiones

- La configuracion Telegram se persiste en PostgreSQL mediante una tabla singleton `telegram_publication_settings`.
- La API de lectura no devuelve el token completo; solo `botTokenConfigured` y `botTokenPreview`.
- Si el administrador guarda el formulario sin token, se conserva el token existente.
- `TelegramPublisher` queda siempre disponible como provider, pero valida `enabled`, `botToken`, `chatId` y `baseUrl` antes de publicar.
- La pantalla `/automation-settings` se convierte en pagina central de configuracion operativa ADMIN.

## Pruebas y verificaciones

- Focal backend: `mvn -q "-Dtest=TelegramPublisherTest,TelegramPublicationSettingsControllerTest" test` OK.
- Focal backend: `mvn -q "-Dtest=PublicationControllerTest" test` OK.
- Backend completo: `mvn test` OK, 227 tests.
- Focal frontend: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include src/app/core/services/application-settings.service.spec.ts --include src/app/features/automation-settings/automation-settings-page.component.spec.ts` OK.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 107 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto.
- n8n: `./n8n/validate-workflows.ps1` OK.

## Notas

- La migracion `V8` fue aplicada en la base local durante `mvn test`.
- La publicacion real en Telegram requiere completar `botToken` y `chatId` desde la pantalla ADMIN.
