# Fecha

2026-06-13

# Objetivo

Ocultar el token de recuperacion en la pantalla de establecimiento de nueva password, manteniendolo solo como dato interno procedente del enlace de correo.

# Contexto

La intervencion corresponde a la Fase 11 del Documento 30, dentro de la tarea T11.10 del Documento 31: usuarios y recuperacion de password en frontend.

# Archivos modificados

- `frontend/src/app/features/auth/reset-password/reset-password-page.component.ts`
- `frontend/src/app/features/auth/reset-password/reset-password-page.component.html`
- `frontend/src/app/features/auth/reset-password/reset-password-page.component.spec.ts`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

# Decisiones

- El token sigue siendo necesario para llamar a `POST /api/v1/auth/reset-password`.
- No se muestra al usuario porque el enlace de recuperacion ya lo entrega como query param.
- Si falta el token, la pantalla muestra un error de enlace invalido o caducado.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/`: 54 specs, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/`: build OK en `frontend/dist/frontend`.
