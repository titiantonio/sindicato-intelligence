# Sprint 1 T1.2 - Validacion de constraints e indices

## Fecha

2026-06-05

## Objetivo

Validar que el esquema MVP creado por `V2__create_mvp_schema.sql` contiene las primary keys, foreign keys, indices y unique constraints requeridos por el Documento 20 y la T1.2 del Documento 31.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 1 - Modelo de Datos MVP.
- T1.2 - Validar primary keys, foreign keys, indexes y unique constraints.

La validacion se realizo sobre PostgreSQL local, con Flyway ya situado en version 2 tras la ejecucion de `V2__create_mvp_schema.sql`.

## Fase MVP relacionada

Documento 30, Fase 2: Modelo de Datos.

## Archivos modificados

- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_05_sprint_1_t1_2_validacion_constraints_indices.md`.

## Validaciones realizadas

Primary keys:

- Confirmadas en las 9 tablas MVP sobre la columna `id`.
- La columna `id` esta definida como `bigint` con secuencia, equivalente operativo de `BIGSERIAL` en PostgreSQL.

Foreign keys:

- `news_articles.source_id -> sources.id`.
- `news_classifications.news_id -> news_articles.id`.
- `event_news.event_id -> events.id`.
- `event_news.news_id -> news_articles.id`.
- `event_ai_analysis.event_id -> events.id`.
- `generated_content.event_id -> events.id`.
- `generated_content.created_by -> users.id`.
- `publications.content_id -> generated_content.id`.

Unique constraints:

- `news_articles.url`.
- `news_articles.hash`.
- `news_classifications.news_id`.
- `event_news.event_id, news_id`.
- `users.email`.

Indices:

- Confirmados los 21 indices `idx_*` definidos para el MVP.

## Documento 31 actualizado

- `[x] T1.2`: validacion de primary keys, foreign keys, indexes y unique constraints completada.

No se marco Sprint 1 como completado porque sigue pendiente T1.4.

## Decisiones tomadas

- No se modificaron migraciones porque la validacion fue correcta.
- No se modifico `backend/pom.xml` ni `CHANGELOG.md` porque no hubo cambio de codigo ni de esquema.

## Pruebas y verificaciones

Se ejecutaron consultas SQL sobre PostgreSQL local mediante `psql` para inspeccionar:

- `information_schema.table_constraints`.
- `information_schema.key_column_usage`.
- `information_schema.constraint_column_usage`.
- `information_schema.columns`.
- `pg_indexes`.

Resultado: validacion correcta.
