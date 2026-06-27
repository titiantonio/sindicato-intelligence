# 2026-06-27 - Optimizacion de pagina de noticias

## Objetivo

Corregir la carga lenta de `/news` moviendo paginacion, filtros y ordenacion al backend, y permitir elegir directamente el numero de pagina.

## Contexto

- Fase MVP afectada: Fase 11, frontend Angular/backoffice, con soporte de API backend.
- Backlog: mejora de mantenimiento sobre Sprint 11 Frontend Angular y API de noticias.
- Se mantiene `GET /api/v1/news` por compatibilidad.
- Nuevo endpoint paginado: `GET /api/v1/news/page`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/ListNewsPageUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsPageQueryRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/news/infrastructure/JpaNewsPageQueryRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsController.java`
- `frontend/src/app/features/news/news-page.component.ts`
- `frontend/src/app/features/news/news-page.component.html`
- `frontend/src/app/core/services/news.service.ts`
- `frontend/src/app/core/models/news.models.ts`
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsControllerTest.java`
- `frontend/src/app/features/news/news-page.component.spec.ts`
- `frontend/src/app/core/services/news.service.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

## Decisiones tomadas

- La pagina 1 muestra las noticias mas recientes por `capturedAt DESC, id DESC`.
- No se modifica ni elimina el endpoint historico `GET /api/v1/news`.
- No se crea migracion Flyway porque se reutilizan indices existentes para `news_articles`, `news_classifications` y `event_news`.
- La tabla de noticias sigue siendo solo consulta y trazabilidad.
- El backend limita `pageSize` a 100 para evitar cargas excesivas.

## Pruebas y verificaciones

- `mvn -q -DskipTests compile` en `backend/`: OK.
- `mvn -q -Dtest=NewsControllerTest test` en `backend/`: OK.
- `npm run build` en `frontend/`: OK. Persisten warnings de budgets existentes en bundle inicial y SCSS de varias pantallas.
- `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend/`: OK, 146 tests ejecutados con exito. Karma aviso que ChromeHeadless no finalizo dentro del margen de cierre, sin fallo de suite.
- `mvn test` completo en `backend/`: ejecutado con fallo no relacionado con `/news`. Fallan `AnalysisControllerTest` por modelo IA real `models/gemma-4-31b-it` en lugar de `deterministic-analysis`, `ClassificationControllerTest` por categoria `OTROS` en lugar de `SIPRI`, `ContentControllerTest` con `502`, y `SecurityConfigTest` por carga de contexto con dependencia `GetGeneratedContentDetailUseCase`.
