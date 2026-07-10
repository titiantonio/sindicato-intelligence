# 2026-07-09 Ampliacion editor WYSIWYG Telegram

## Fecha

2026-07-09

## Objetivo

Ampliar y profesionalizar el editor WYSIWYG del dialogo de publicaciones manuales Telegram, incluyendo formato y emotes tambien en el titulo.

## Contexto

Mantenimiento correctivo de Fase 10 y Fase 11 sobre el flujo de publicaciones manuales Telegram.

Se verifico la documentacion oficial de Telegram Bot API. El flujo actual del backend usa `sendMessage` con `parse_mode=HTML`, por lo que el editor se ajusta a las etiquetas HTML soportadas por ese modo: formato inline, enlaces, menciones, custom emoji, entidad tiempo, codigo, bloque de codigo, citas y citas expandibles.

## Fase MVP

- Fase 10: publicacion Telegram.
- Fase 11: backoffice Angular.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.scss`
- `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/TelegramPublisher.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/infrastructure/TelegramPublisherTest.java`
- `backend/pom.xml`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Mantener una sola toolbar para titulo y mensaje; el formato se aplica al campo activo.
- Incluir contadores de caracteres para titulo y mensaje.
- No introducir dependencias nuevas de editor enriquecido.
- Usar botones y paneles propios para entidades Telegram: enlaces, menciones, custom emoji, tiempo, bloques y emotes.
- Ampliar el saneado backend para aceptar solo etiquetas y atributos compatibles con `parse_mode=HTML`.
- No implementar rich messages avanzados fuera de `sendMessage`, porque requieren un flujo API distinto al de publicacion actual.
- Subir backend a `0.0.87-SNAPSHOT` y frontend a `0.0.18`.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- `./mvnw.cmd "-Dtest=TelegramPublisherTest" test` OK, 6 tests.
- `git diff --check` OK, solo avisos LF/CRLF propios del entorno Windows.
