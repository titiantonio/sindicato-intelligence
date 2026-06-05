# Sprint 1 T1.1 - Esquema MVP con Flyway

## Fecha

2026-06-05

## Objetivo

Crear la migracion Flyway del esquema completo MVP definido en el Documento 20 y alineado con el Documento 30, sin modificar `V1__initial_schema.sql`.

## Contexto

Sprint 0 estaba completado y la base local tenia aplicada solo `V1__initial_schema.sql` con `system_info`.

La tarea seleccionada en el Documento 31 fue:

- Sprint 1 - Modelo de Datos MVP.
- T1.1 - Crear migracion `V2__create_mvp_schema.sql`.

Durante la verificacion tambien se completo:

- T1.3 - Verificar ejecucion Flyway, porque Flyway aplico V2 y la consulta de tablas mostro las tablas MVP.

## Fase MVP relacionada

Documento 30, Fase 2: Modelo de Datos.

## Archivos modificados

- `backend/src/main/resources/db/migration/V2__create_sources_and_news.sql`.
- `backend/src/main/resources/db/migration/V2__create_mvp_schema.sql`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_05_sprint_1_t1_1_esquema_mvp_flyway.md`.

## Decisiones tomadas

- Se sustituyo el placeholder versionado `V2__create_sources_and_news.sql`, que estaba vacio, por `V2__create_mvp_schema.sql`.
- No se modifico `V1__initial_schema.sql`.
- Se crearon las tablas MVP oficiales: `sources`, `news_articles`, `news_classifications`, `events`, `event_news`, `event_ai_analysis`, `generated_content`, `publications` y `users`.
- Se usaron claves primarias `BIGSERIAL` y fechas `TIMESTAMP WITH TIME ZONE`.
- Se usaron campos `JSONB` para estructuras generadas o devueltas por IA.
- Se definieron claves externas explicitas entre las tablas del modelo fisico.
- Se aplicaron constraints `UNIQUE` e indices definidos en el Documento 20.
- Se incremento la version backend a `0.0.2-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T1.1`: migracion `V2__create_mvp_schema.sql` creada.
- `[x] T1.3`: ejecucion Flyway verificada en PostgreSQL local.

No se marco Sprint 1 como completado porque siguen pendientes T1.2 y T1.4.

## Pruebas y verificaciones

Verificaciones realizadas:

- Arranque Spring Boot con Flyway activado. Flyway valido 2 migraciones y aplico `V2__create_mvp_schema.sql` correctamente.
- Consulta de `flyway_schema_history`: V1 y V2 aparecen con `success = true`.
- Consulta de tablas en `public`: aparecen las tablas MVP junto con `system_info` y `flyway_schema_history`.
- Consulta de indices `idx_%`: aparecen los 21 indices definidos para el MVP.
- Ejecucion de `mvnw test`: `BUILD SUCCESS`, 1 test ejecutado, 0 fallos.

Incidencia menor durante la verificacion:

- Un primer comando Maven fallo por interpretacion de PowerShell del parametro `-Dspring-boot.run.arguments`; se repitio con el parametro correctamente entrecomillado.
- El arranque en modo no web aplico Flyway correctamente, pero fallo despues por `SecurityConfig` al no existir `HttpSecurity` en contexto no web. La verificacion final se completo con `mvnw test`, que arranco el contexto web de test correctamente.
