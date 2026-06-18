# 2026-06-18 - Ajuste de anchos en tablas del backoffice

## Objetivo

Revisar todas las tablas de visualizacion del frontend para compactar columnas de contenido corto y evitar cortes o saltos innecesarios en campos de lectura frecuente como nombre, email, titulo, URL, estado y fechas.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular.
- Sprint relacionado: Sprint 12 cerrado, ajuste visual final documentado como `T12.25`.
- No se modifican contratos REST, servicios Angular ni reglas de negocio.

## Archivos modificados

- `frontend/src/styles.scss`
- `frontend/src/app/features/dashboard/dashboard-page.component.html`
- `frontend/src/app/features/dashboard/dashboard-page.component.scss`
- `frontend/src/app/features/events/events-page.component.html`
- `frontend/src/app/features/events/events-page.component.scss`
- `frontend/src/app/features/events/event-detail-page.component.html`
- `frontend/src/app/features/events/event-detail-page.component.scss`
- `frontend/src/app/features/content/content-page.component.html`
- `frontend/src/app/features/content/content-page.component.scss`
- `frontend/src/app/features/audit/audit-page.component.html`
- `frontend/src/app/features/audit/audit-page.component.scss`
- `frontend/src/app/features/sources/sources-page.component.html`
- `frontend/src/app/features/sources/sources-page.component.scss`
- `frontend/src/app/features/users/users-page.component.html`
- `frontend/src/app/features/users/users-page.component.scss`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.scss`
- `backend/src/test/java/es/sindicato/intelligence/ai/infrastructure/JpaAiObservabilityRepositoryTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se usan `colgroup` por tabla para declarar anchos de columnas de forma estable y sin inflar los SCSS de componentes.
- Se anaden utilidades globales `table-cell-nowrap` y `table-cell-break` para controlar saltos de linea de forma explicita.
- Campos cortos como ID, noticias, prioridad, estado, fechas, latencia y acciones quedan compactos.
- Campos principales como titulos, URLs, detalles de auditoria y errores IA conservan espacio suficiente y pueden partir cuando el contenido sea largo.
- Nombres y emails quedan en una sola linea para mejorar lectura en usuarios y fuentes.
- Se corrige `JpaAiObservabilityRepositoryTest` para no depender de datos residuales de metricas IA en la base compartida de integracion.

## Pruebas y verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 113 tests.
- `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.
- `mvn "-Dtest=JpaAiObservabilityRepositoryTest" test` OK, 2 tests.
- `mvn test` OK, 234 tests y Flyway valida 9 migraciones.

## Documento 31

- Anadida `T12.25` como refinamiento visual final de tablas del backoffice.
