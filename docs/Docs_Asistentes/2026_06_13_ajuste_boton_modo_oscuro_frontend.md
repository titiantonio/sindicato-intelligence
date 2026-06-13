# Fecha

2026-06-13

# Objetivo

Evitar que el boton de modo oscuro se superponga con el boton de logout y ubicarlo en la cabecera del backoffice como icono.

# Contexto

La intervencion corresponde a la Fase 11 del Documento 30, dentro del bloque Frontend Angular del Documento 31. Es un ajuste visual sobre el modo claro/oscuro ya implementado.

# Archivos modificados

- `frontend/src/app/app.html`
- `frontend/src/app/app.scss`
- `frontend/src/app/app.ts`
- `frontend/src/app/layout/shell/shell.component.ts`
- `frontend/src/app/layout/shell/shell.component.html`
- `frontend/src/app/layout/shell/shell.component.scss`
- `CHANGELOG.md`

# Decisiones

- Se cambia el interruptor textual por un boton circular con icono de luna en modo claro y sol en modo oscuro.
- Se integra en la cabecera del `ShellComponent`, antes del bloque de usuario y logout, para evitar solapes.
- Se mantienen `aria-label`, `aria-pressed` y `title` para accesibilidad.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/`: 56 specs, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/`: build OK en `frontend/dist/frontend`.
