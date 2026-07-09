# 2026-07-09 Correccion publicaciones manuales Telegram

## Fecha

2026-07-09

## Objetivo

Corregir el flujo de publicaciones manuales Telegram para permitir uso por `EDITOR`, mejorar errores de adjuntos, registrar fallos, mostrar autor, configurar limites de subida desde ADMIN y limpiar el dialogo frontend.

## Contexto

Intervencion de mantenimiento correctivo sobre Fases 10, 11 y 12 del MVP, asociada al bloque 19.16 del Documento 31.

## Archivos modificados

- Backend Spring Boot: modulo `publication`, configuracion Telegram, auditoria, dashboard y migraciones Flyway `V19`/`V20`.
- Frontend Angular: modelos/servicios de publicaciones y settings, pantalla `/publications`, detalle de publicacion y pantalla `/settings`.
- Versionado: `backend/pom.xml`, `frontend/package.json`, `frontend/package-lock.json` y `CHANGELOG.md`.
- Backlog: Documento 31.

## Decisiones

- Se mantiene `/api/v1/settings/telegram` restringido a `ADMIN`.
- Se anade `GET /api/v1/publications/telegram-destinations` para lectura operativa de destinos activos por `ADMIN` y `EDITOR`, sin exponer `chatId`.
- El limite HTTP multipart queda como techo tecnico alto; los limites visibles se aplican desde configuracion Telegram persistida.
- Los fallos funcionales de adjuntos crean publicacion `FAILED`, targets fallidos y auditoria antes de devolver error descriptivo.
- El editor manual usa HTML compatible con Telegram y el publicador envia `parse_mode=HTML`.

## Pruebas o verificaciones

- `./mvnw.cmd "-Dtest=PublicationControllerTest" test`: OK, 6 tests.
- `./mvnw.cmd "-Dtest=TelegramPublicationSettingsControllerTest,TelegramPublisherTest,JpaTelegramPublicationSettingsRepositoryTest" test`: OK, 10 tests.
- `npx.cmd tsc -p tsconfig.app.json --noEmit`: OK.
- `npm.cmd test ...` y `npm.cmd run build`: agotaron timeout del entorno sin devolver diagnostico ni dejar proceso colgado.
- `./mvnw.cmd "-Dtest=DashboardControllerTest" test`: fallo preexistente/no relacionado por datos reales locales en PostgreSQL que alteran el orden esperado de eventos prioritarios.
