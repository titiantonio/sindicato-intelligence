# T9.3 TelegramPublisher

## Fecha

2026-06-09

## Objetivo

Implementar el adaptador tecnico de publicacion Telegram para el Sprint 9.

## Contexto

Se revisaron el Documento 31 para T9.3, el Documento 18 sobre `TelegramPublisher`, el Documento 09 V2.0 sobre reintentos y registro de errores Telegram, y la skill de logging para evitar exponer secretos.

## Fase MVP

Fase 10: Publicacion.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/TelegramPublisher.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/infrastructure/TelegramPublisherTest.java`
- `backend/src/main/resources/application.yml`
- `CHANGELOG.md`

## Decisiones

- `TelegramPublisher` implementa `PublishingProvider` y se activa con `app.publication.telegram.enabled=true`.
- Las credenciales se configuran por variables `TELEGRAM_ENABLED`, `TELEGRAM_BOT_TOKEN`, `TELEGRAM_CHAT_ID` y `TELEGRAM_BASE_URL`.
- Los logs registran `contentId`, canal, estado y errores, pero no token, chat id ni payload completo.
- El `responsePayload` persistible se acota a informacion operativa minima: `ok`, `messageId`, `statusCode` y descripcion de error.

## Pruebas o verificaciones

- Se anaden pruebas unitarias del adaptador con `MockRestServiceServer` para exito, soporte de canal, configuracion incompleta y error HTTP.
- Verificado con `mvn "-Dtest=TelegramPublisherTest" test`: 4 tests ejecutados, 0 fallos, 0 errores.
- Una primera ejecucion fallo por una asercion fragil del orden de claves JSON; se corrigio el test para validar contenido sin depender del orden.
