# 2026-06-27 - Nueva pagina de noticias

## Objetivo

Crear una pagina `/news` en el backoffice Angular para consultar todas las noticias capturadas con tabla operativa, filtros, ordenacion, paginacion y navegacion a detalle.

## Contexto

- Fase MVP afectada: Fase 11, frontend Angular/backoffice.
- Backlog: mejora de mantenimiento sobre Sprint 11 Frontend Angular en el Documento 31.
- Contrato usado: `GET /api/v1/news` existente, sin cambios backend funcionales.
- Fuente mostrada como `Fuente #id` porque el contrato actual no expone nombre de fuente para `ADMIN` y `EDITOR`.

## Archivos modificados

- `frontend/src/app/features/news/news-page.component.ts`
- `frontend/src/app/features/news/news-page.component.html`
- `frontend/src/app/features/news/news-page.component.scss`
- `frontend/src/app/features/news/news-page.component.spec.ts`
- `frontend/src/app/core/services/news.service.ts`
- `frontend/src/app/core/services/news.service.spec.ts`
- `frontend/src/app/core/models/news.models.ts`
- `frontend/src/app/app.routes.ts`
- `frontend/src/app/layout/shell/shell.component.ts`
- `frontend/src/app/features/news/news-detail-page.component.html`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

## Decisiones tomadas

- No se amplia backend ni DTO de noticias.
- La pantalla es solo de consulta y trazabilidad; no anade acciones editoriales sobre noticias.
- La nueva entrada de sidebar `Noticias` queda visible para `ADMIN` y `EDITOR`.
- El detalle de noticia vuelve a `/news`.

## Pruebas y verificaciones

- `npm run build` en `frontend/`: OK. El build conserva warnings de budgets existentes en bundle inicial y SCSS de varias pantallas.
- `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend/`: OK, 144 tests ejecutados con exito. Karma aviso que ChromeHeadless no finalizo dentro del margen de cierre, sin fallo de suite.
