# Fallback WF-02 y descarte manual de noticias

## Fecha

2026-07-11

## Objetivo

Evitar que noticias fuera de ambito con respuesta Gemini sin texto queden reintentandose indefinidamente en `WF-02`, y anadir acciones manuales de descarte/restauracion en la ventana `/news`.

## Contexto

- Fase MVP relacionada: Fase 6 Clasificacion IA y Fase 11 Frontend Angular.
- Workflow relacionado: `WF-02-Classify-News` en Spring Boot.
- Incidencia motivadora: `newsId=2927` y duplicada `3065`, noticias de sucesos fuera de ambito con fallos repetidos `Gemini response does not contain candidates[0].content.parts[0].text`.
- Tarea registrada en Documento 31: `19.30 Fallback WF-02 y descarte manual de noticias`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsArticle.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/DiscardNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/RestoreDiscardedNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsController.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsPageItem.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsPageItemResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/infrastructure/JpaNewsPageQueryRepository.java`.
- `backend/src/main/java/es/sindicato/intelligence/audit/application/AuditDetailFormatter.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsControllerTest.java`.
- `frontend/src/app/core/models/news.models.ts`.
- `frontend/src/app/core/services/news.service.ts`.
- `frontend/src/app/core/services/news.service.spec.ts`.
- `frontend/src/app/features/news/news-page.component.ts`.
- `frontend/src/app/features/news/news-page.component.html`.
- `frontend/src/app/features/news/news-page.component.scss`.
- `frontend/src/app/features/news/news-page.component.spec.ts`.
- `backend/pom.xml`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- El fallback solo se aplica cuando el error es especificamente ausencia de texto en Gemini y la noticia no contiene senales educativas ni sindicales.
- Si hay senales educativas, el fallo se mantiene para permitir reintento normal y evitar falsos descartes.
- La restauracion manual no requiere migracion: si la noticia conserva asociacion a evento vuelve a `EVENT_MATCHED`; si conserva clasificacion vuelve a `CLASSIFIED`; si no, vuelve a `CAPTURED`.
- La tabla paginada de noticias expone `url` para permitir la accion `Original` sin llamada adicional.
- Las acciones manuales registran auditoria editorial con `NEWS_DISCARDED` y `NEWS_RESTORED`.

## Pruebas y verificaciones

- Backend unitario clasificacion: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest" test` OK, 9 tests.
- Backend API noticias: `mvnw.cmd "-Dtest=NewsControllerTest" test` OK, 15 tests.
- Backend focal completo: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest,NewsControllerTest" test` OK, 24 tests.
- Frontend focal noticias: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/news/news-page.component.spec.ts --include=src/app/core/services/news.service.spec.ts` OK, 14 tests.

## Notas

- No se modificaron migraciones Flyway porque no se anadieron columnas ni tablas.
- No se actualizaron datos existentes de `2927` ni `3065`; quedaran corregidas en la siguiente ejecucion de WF-02 o pueden descartarse manualmente desde `/news`.
