# Consolidacion Flyway fuentes RSS

## Fecha

2026-06-07

## Objetivo

Consolidar las migraciones Flyway iniciales del MVP y convertir la carga temporal de fuentes RSS en un seed versionado.

## Contexto

El proyecto esta en fase de desarrollo y se acordo aplicar la opcion de resetear la base de datos local para poder reescribir las migraciones iniciales. Esto permite dejar un historial Flyway limpio antes de generar entornos persistentes.

## Fase MVP

- Documento 30, Fase 2: modelo de datos.
- Documento 30, Fase 3: modulo Sources.
- Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Archivos modificados

- `backend/src/main/resources/db/migration/V1__create_mvp_schema.sql`.
- `backend/src/main/resources/db/migration/V2__seed_admin_user.sql`.
- `backend/src/main/resources/db/migration/V3__seed_rss_sources.sql`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_07_consolidacion_flyway_fuentes_rss.md`.

## Archivos eliminados o sustituidos

- `backend/src/main/resources/db/migration/V1__initial_schema.sql` fue sustituida por `V1__create_mvp_schema.sql`.
- `backend/src/main/resources/db/migration/V2__create_mvp_schema.sql` fue consolidada en `V1__create_mvp_schema.sql`.
- `backend/src/main/resources/db/migration/V3__seed_data.sql` fue sustituida por `V2__seed_admin_user.sql`.
- `backend/src/main/resources/db/migration/V4__add_event_news_confidence_score.sql` fue integrada en `V1__create_mvp_schema.sql`.

## Decisiones

- Se elimino la tabla tecnica `system_info` porque no forma parte del modelo MVP ni esta usada por el codigo.
- Se agrego `CONSTRAINT uk_sources_url UNIQUE (url)` directamente en la creacion inicial de `sources`.
- Se integro `event_news.confidence_score` en la creacion inicial de `event_news`.
- Se integro tambien `CONSTRAINT ck_event_news_confidence_score CHECK (confidence_score IS NULL OR confidence_score BETWEEN 0 AND 100)`.
- Se creo `V3__seed_rss_sources.sql` con las 54 fuentes RSS actuales, sin insertar IDs manuales.
- El seed de fuentes usa `ON CONFLICT (url) DO UPDATE` para mantener idempotencia respecto a la URL.
- Se incremento la version del backend a `0.0.16-SNAPSHOT`.

## Operacion sobre BBDD local

- Se paro y elimino el contenedor local `sindicato-postgres`.
- Se elimino el volumen local `database_postgres_data`.
- Se recreo solo el servicio `postgres` con `docker compose up -d postgres`.
- No se elimino el volumen de n8n.

## Pruebas y verificaciones

- Ejecutado `mvn test` desde `backend`.
- Resultado: `Tests run: 94, Failures: 0, Errors: 0, Skipped: 0`.
- Flyway valido y aplico 3 migraciones desde cero:
  - `V1 - create mvp schema`.
  - `V2 - seed admin user`.
  - `V3 - seed rss sources`.
- Verificado `sources`: 54 filas, IDs `1..54`, prioridades `1..54`.
- Verificadas constraints `uk_sources_url` y `ck_event_news_confidence_score`.
- Verificado que `system_info` no existe en el esquema publico.

## Resultado

La base de datos de desarrollo queda reconstruida desde una secuencia Flyway limpia y las fuentes RSS quedan versionadas como seed inicial del MVP.
