# Fecha

2026-07-25

# Objetivo

Corregir el desbordamiento horizontal de la tabla de operaciones IA en la pagina ADMIN `/settings` cuando el modelo contiene identificadores largos.

# Contexto

La intervencion corresponde a mantenimiento correctivo posterior al Sprint 12 sobre Fase 11/12 del MVP. La tabla de operaciones IA forzaba `minWidth="84rem"` y la celda `Modelo` usaba `nowrap`, por lo que valores como `models/gemma-4-31b-it:conservative-recitation-fallback` ensanchaban la tabla mas alla de la pantalla.

# Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.scss`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se mantuvo el componente compartido `app-standard-table` sin cambios globales.
- El ajuste se limito a la tabla de operaciones IA mediante clase local.
- La columna `Modelo` pasa a permitir salto de texto con `overflow-wrap:anywhere` y `word-break:break-word`.
- La tabla deja de declarar un ancho minimo fijo de `84rem` y usa `100%` para no generar overflow global por defecto.

# Pruebas o verificaciones

- Se anadio prueba de regresion en `SettingsPageComponent` para un nombre de modelo largo.
- Frontend focal settings: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts` OK, 16 tests.
- Frontend build: `npm.cmd run build` OK.
- Observacion: Karma mantuvo el aviso no bloqueante conocido de cierre lento de `ChromeHeadless`.
