# Publicaciones manuales Telegram con multimedia

## Fecha

2026-06-30.

## Objetivo

Implementar publicaciones manuales para Telegram desde el backoffice, con multiples destinos configurables y soporte de adjuntos multimedia.

## Contexto

Intervencion de mantenimiento evolutivo sobre Fase 10, Fase 11 y Fase 12 del MVP. Telegram sigue siendo el unico canal operativo; el modelo queda preparado para ampliar canales mas adelante sin implementar Twitter/X.

## Archivos modificados

- Backend publicaciones: dominio, aplicacion, infraestructura y API del modulo `publication`.
- Base de datos: `backend/src/main/resources/db/migration/V18__telegram_destinations_manual_publications.sql`.
- Frontend: modelos, servicios, `/settings`, `/publications` y detalle de publicacion.
- Documentacion/versionado: `CHANGELOG.md`, `backend/pom.xml` y Documento 31.

## Decisiones tomadas

- Se anade `telegram_publication_destinations` para sustituir operativamente el `chat_id` unico sin romper compatibilidad.
- Las publicaciones manuales se guardan en `publications` con `publication_type = MANUAL_MESSAGE`.
- Cada envio por destino se registra en `publication_targets`.
- Los adjuntos se guardan en disco local configurable y PostgreSQL conserva solo metadatos en `publication_attachments`.
- El endpoint manual usa `multipart/form-data`.
- No se permiten Chat ID libres en el envio manual; deben estar configurados previamente por ADMIN.

## Pruebas y verificaciones

- `./mvnw.cmd -q -DskipTests compile`: OK.
- `./mvnw.cmd "-Dtest=PublicationControllerTest,TelegramPublisherTest,TelegramPublicationSettingsControllerTest,JpaTelegramPublicationSettingsRepositoryTest,JpaPublicationRepositoryTest" test`: OK, 14 tests.
- `./mvnw.cmd "-Dtest=PublicationControllerTest" test`: OK, 4 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests.
- `npm.cmd run build`: OK.
- `./mvnw.cmd test`: ejecutado; queda 1 fallo no relacionado en `DashboardControllerTest.ordersPriorityEventsByImpactNewsCountAndLastUpdate` por datos reales locales en PostgreSQL que alteran el orden esperado del fixture.
