# Fecha

2026-06-13

# Objetivo

Anadir modo oscuro global para todo el frontend Angular del backoffice.

# Contexto

La intervencion corresponde a la Fase 11 del Documento 30, dentro del bloque Frontend Angular del Documento 31. El cambio afecta a la experiencia visual transversal y no modifica contratos API ni reglas de negocio.

# Archivos modificados

- `frontend/src/app/core/services/theme.service.ts`
- `frontend/src/app/core/services/theme.service.spec.ts`
- `frontend/src/app/app.ts`
- `frontend/src/app/app.html`
- `frontend/src/app/app.scss`
- `frontend/src/styles.scss`
- SCSS de layout, autenticacion, dashboard, eventos, contenido, publicaciones, fuentes, usuarios, auditoria y componentes compartidos.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

# Decisiones

- Se centraliza el tema con variables CSS en `styles.scss`.
- Se persiste la preferencia en `localStorage` con clave `sindicato-theme`.
- Si no hay preferencia guardada, se respeta `prefers-color-scheme`.
- El interruptor se ubica en `App` para estar disponible tambien en login, recuperacion y cambio de password.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/`: 56 specs, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/`: build OK en `frontend/dist/frontend`.
- Verificacion headless con Chrome DevTools sobre `http://localhost:4200/login` y `http://localhost:4200/dashboard`: tema oscuro aplicado a body, tarjetas e interruptor; verificacion adicional forzando `light` y `dark` en login con colores computados correctos.
