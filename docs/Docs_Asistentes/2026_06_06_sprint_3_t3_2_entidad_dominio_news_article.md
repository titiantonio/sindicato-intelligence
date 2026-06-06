# Sprint 3 T3.2 - Entidad de dominio NewsArticle

## Fecha

2026-06-06

## Objetivo

Crear la entidad de dominio `NewsArticle` y el value object `NewsStatus` para el modulo `news`.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.2: entidad dominio `NewsArticle`.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsArticle.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsStatus.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/domain/NewsArticleTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_2_entidad_dominio_news_article.md`.

## Decisiones tomadas

- Se mantuvo dominio puro, sin Spring, JPA, HTTP ni DTOs.
- Se usaron los estados definitivos `CAPTURED`, `CLASSIFIED`, `EVENT_MATCHED` y `ARCHIVED` por coherencia con Documento 20 y AGENTS.md.
- Se incluyeron campos de auditoria y `hash` porque forman parte del modelo fisico MVP vigente.

## Documento 31 actualizado

- `[x] T3.2`: entidad de dominio `NewsArticle` completada.

## Pruebas y verificaciones

- Se crearon pruebas unitarias de dominio.
- La suite completa se ejecutara al cierre del Sprint 3.
