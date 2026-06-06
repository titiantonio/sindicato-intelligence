# Sprint 3 T3.4 - Entidad JPA NewsArticleEntity

## Fecha

2026-06-06

## Objetivo

Crear la entidad JPA `NewsArticleEntity` para mapear la tabla `news_articles` del modelo fisico MVP.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.4: entidad JPA.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/infrastructure/NewsArticleEntity.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/infrastructure/NewsArticleEntityTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_4_entidad_jpa_news_article.md`.

## Decisiones tomadas

- Se mapearon las columnas de `news_articles` segun `V2__create_mvp_schema.sql` y Documento 20.
- Se uso `OffsetDateTime` para columnas `TIMESTAMP WITH TIME ZONE`.
- Se almaceno `processing_status` como enum textual mediante `EnumType.STRING`.
- No se incluyo logica de negocio en la entidad JPA.

## Documento 31 actualizado

- `[x] T3.4`: entidad JPA completada.

## Pruebas y verificaciones

- Se creo prueba de mapeo JPA por reflexion.
- La suite completa se ejecutara al cierre del Sprint 3.
