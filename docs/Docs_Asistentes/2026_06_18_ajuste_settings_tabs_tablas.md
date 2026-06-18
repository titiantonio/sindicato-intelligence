# 2026-06-18 - Ajuste de settings con tabs y tablas operativas

## Objetivo

Refinar el cierre de Sprint 12 para que la pagina ADMIN `/settings` separe las configuraciones por areas y para que las tablas de prompts y metricas IA tengan filtros, ordenacion y paginacion como el resto del backoffice.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular y cierre Sprint 12 de optimizacion y observabilidad IA.
- `WF-01` permanece en n8n.
- `WF-02` a `WF-06` permanecen migrados a backend Spring Boot.
- No se habilita edicion de prompts desde UI; solo consulta operativa.

## Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.scss`
- `frontend/src/app/features/settings/settings-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- `/settings` queda estructurada en tres tabs: IA y prompts, proveedores de publicacion y automatizaciones.
- La tabla de prompts permite filtrar y ordenar por clave, nombre, modulo, version, checksum, estado y fecha de creacion.
- La tabla de metricas IA permite filtrar y ordenar por todos sus campos visibles, incluyendo estado, entidad, latencia y error.
- La paginacion se mantiene en cliente porque los endpoints actuales devuelven snapshots administrativos acotados.
- Se incrementa el backend a `0.0.56-SNAPSHOT` para registrar cambio de codigo frontend y documentacion asociada.

## Pruebas y verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts` OK, 7 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 113 tests.
- `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`; `settings-page.component.scss` queda bajo presupuesto.
- `mvn test` OK, 234 tests y Flyway valida 9 migraciones.
- `.\n8n\validate-workflows.ps1` OK para `WF-01`.

## Documento 31

- Anadida `T12.24` como refinamiento final de `/settings` con tabs, filtros, ordenacion y paginacion en prompts/metricas IA.
