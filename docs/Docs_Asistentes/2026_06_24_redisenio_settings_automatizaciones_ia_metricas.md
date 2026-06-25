# Fecha

2026-06-24

# Objetivo

Redisenar la pagina ADMIN `/settings` para separar metricas IA, prompts, automatizaciones y publicacion, dejando `Metricas IA` como pestana predeterminada.

# Contexto

La tarea corresponde a un refinamiento posterior al Sprint 12 del MVP, sobre la configuracion ADMIN de IA, automatizaciones internas y observabilidad. No se modifican contratos backend, migraciones ni decisiones arquitectonicas.

# Fase MVP

Fase 12: optimizacion IA, automatizaciones internas, observabilidad y configuracion ADMIN.

# Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.scss`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `frontend/src/app/shared/components/metric-card/metric-card.component.scss`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- `/settings` abre en `Metricas IA` para priorizar la observabilidad operativa.
- `Prompts IA` queda limitado al versionado tecnico de prompts.
- `Automatizaciones` agrupa configuracion operativa y configuracion IA por workflow, incluyendo proveedores/modelos.
- `Publicacion` queda separada y contiene solo Telegram.
- Las tarjetas de metricas se ajustan para evitar cortes visuales y mostrar operaciones, calidad, errores y rendimiento.

# Pruebas o verificaciones

- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/shared/components/metric-card/metric-card.component.spec.ts` OK, 13 tests.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `users`, `sources` y `audit`; sin warning nuevo de `settings`.
- Verificacion visual local en `http://localhost:4200/settings` OK con login de desarrollo: `Metricas IA` abre por defecto, hay 4 cards, `Automatizaciones` contiene proveedores/modelos IA y no se detectan desbordes relevantes.
