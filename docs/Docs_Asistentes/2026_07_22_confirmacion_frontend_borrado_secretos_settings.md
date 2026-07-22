# Fecha

2026-07-22

# Objetivo

Sustituir las confirmaciones nativas de Windows/navegador al eliminar la API key IA y el bot token de Telegram por un dialogo propio del backoffice.

# Contexto

Correccion de usabilidad sobre `/settings`, posterior al refinamiento de gestion ADMIN de secretos de IA y Telegram. El proyecto usa modales `p-dialog` en el backoffice para confirmaciones operativas, por lo que `window.confirm` no era coherente con el patron visual existente.

# Fase MVP

Fase 12 / centro ADMIN `/settings`.

# Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se elimina el uso de `window.confirm`.
- Se anade un estado `pendingSecretDeletion` para abrir un `p-dialog` de confirmacion.
- El borrado solo se ejecuta al confirmar en el modal del frontend.
- Los botones de confirmar y cancelar se renderizan como contenido normal del dialogo para evitar que PrimeNG oculte el `footer` proyectado.
- Se mantiene el contrato backend existente con `clearApiKey` y `clearBotToken`.

# Pruebas o verificaciones

- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts` OK, 15 tests.
- Frontend build: `npm.cmd run build` OK.
- Nota: Karma mostro aviso no bloqueante de cierre tardio de `ChromeHeadless` tras finalizar los tests correctamente.
