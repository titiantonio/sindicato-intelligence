# Fecha

2026-07-25

# Objetivo

Simplificar y reordenar la tabla de operaciones IA en la pagina ADMIN `/settings`.

# Contexto

La intervencion corresponde a mantenimiento correctivo posterior al Sprint 12 sobre Fase 11/12 del MVP. La tabla mostraba columnas `WF` y `Operacion`, pero la columna `Prompt` ya identifica suficientemente el workflow funcional para el uso operativo diario.

# Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se eliminaron solo las columnas visibles redundantes `WF` y `Operacion`.
- La tabla queda organizada como `Fecha`, `Prompt`, `Estado`, `Proveedor`, `Modelo`, `Entidad`, `Latencia` y `Error`.
- Se mantiene la informacion tecnica completa en el detalle de operacion abierto al pulsar la fila.
- No se modifican contratos API, modelos de datos ni logica de negocio.

# Pruebas o verificaciones

- Se anadio prueba de regresion para asegurar que no se renderizan las columnas redundantes.
- Frontend focal settings: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts` OK, 17 tests.
- Frontend build: `npm.cmd run build` OK.
- Observacion: Karma mantuvo el aviso no bloqueante conocido de cierre lento de `ChromeHeadless`.
