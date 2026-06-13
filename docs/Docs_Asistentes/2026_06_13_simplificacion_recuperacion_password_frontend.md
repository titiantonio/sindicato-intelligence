# Fecha

2026-06-13

# Objetivo

Simplificar la pantalla de recuperacion de password para que solo muestre un boton: envio del enlace de restablecimiento de password olvidada.

# Contexto

La intervencion corresponde a la Fase 11 del Documento 30, dentro de la tarea T11.10 del Documento 31: usuarios y recuperacion de password en frontend.

# Archivos modificados

- `frontend/src/app/features/auth/forgot-password/forgot-password-page.component.ts`
- `frontend/src/app/features/auth/forgot-password/forgot-password-page.component.html`
- `frontend/src/app/features/auth/forgot-password/forgot-password-page.component.spec.ts`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

# Decisiones

- Se elimina de la vista publica de recuperacion el boton para solicitar password temporal.
- Se mantiene el servicio `requestTemporaryPassword` porque pertenece a flujos administrativos ya existentes y no era necesario cambiar contratos backend.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/`: 52 specs, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/`: build OK en `frontend/dist/frontend`.
