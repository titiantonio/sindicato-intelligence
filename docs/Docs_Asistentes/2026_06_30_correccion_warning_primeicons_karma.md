# Fecha

2026-06-30

# Objetivo

Eliminar el warning de Karma causado por los 404 de fuentes PrimeIcons durante `npm test`.

# Contexto

La intervencion corresponde a la Fase 11 del Documento 30, como mantenimiento correctivo del backoffice Angular posterior a la modernizacion PrimeNG + Tailwind. En el Documento 31 el problema figuraba como deuda no bloqueante en las tareas 19.12 y 19.13.

# Fase MVP

Fase 11 - Frontend Angular.

# Archivos modificados

- `frontend/angular.json`
- `frontend/karma.conf.cjs`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_30_correccion_warning_primeicons_karma.md`

# Decisiones

- Se configuro `karmaConfig` para el target de tests Angular.
- Se creo un middleware de Karma limitado a `/base/media/primeicons.woff2`, `/base/media/primeicons.woff` y `/base/media/primeicons.ttf`.
- Las fuentes se leen desde `node_modules/primeicons/fonts`, evitando copiar binarios al repositorio.
- No se modifico el CSS de produccion ni la configuracion de build.
- Se actualizo la version frontend a `0.0.12`.

# Pruebas o verificaciones

- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. No aparecen los 404 de PrimeIcons.
- `npm run build`: OK sin warnings de budget. Bundle inicial: `527.50 kB`.
- Persiste el warning no relacionado de cierre lento de ChromeHeadless.
