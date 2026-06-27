# Fecha

2026-06-27

# Objetivo

Mostrar fuentes descriptivas en la pagina de noticias y en el detalle de noticia, evitando etiquetas tecnicas como `Fuente #9`.

# Contexto

La tarea corresponde a la Fase 11 del Documento 30, backoffice Angular integrado con APIs reales. En el Documento 31 se registra como mejora correctiva sobre la pagina `/news`, creada previamente con el fallback `Fuente #id` por no exponer nombres de fuente en el contrato paginado.

# Fase MVP

Fase 11 - Frontend Angular / backoffice operativo.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsPageItem.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/GetNewsTraceUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/infrastructure/JpaNewsPageQueryRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsPageItemResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsController.java`
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsResponseTest.java`
- `frontend/src/app/core/models/news.models.ts`
- `frontend/src/app/features/news/news-page.component.ts`
- `frontend/src/app/features/news/news-page.component.html`
- `frontend/src/app/features/news/news-detail-page.component.ts`
- `frontend/src/app/features/news/news-detail-page.component.html`
- `frontend/src/app/features/news/news-page.component.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se amplia el contrato de noticias con `sourceName` manteniendo `sourceId` para trazabilidad tecnica.
- El listado paginado une `news_articles` con `sources` para filtrar, buscar y ordenar por nombre descriptivo.
- Angular mantiene fallback a `Fuente #id` solo si el backend no entrega nombre de fuente.
- No se anade migracion Flyway porque no hay cambio de esquema.

# Pruebas o verificaciones

- `mvn -q "-Dtest=NewsControllerTest,NewsResponseTest" test` ejecutado en `backend/` con resultado OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/news/news-page.component.spec.ts --include=src/app/core/services/news.service.spec.ts` ejecutado en `frontend/` con resultado OK: 10 tests, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/` con resultado OK. Persisten warnings preexistentes de budgets en bundle inicial y SCSS de varias pantallas.
