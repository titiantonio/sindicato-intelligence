# Reconsolidacion Flyway de desarrollo

Fecha: 2026-07-20

## Objetivo

Unificar las migraciones Flyway del backend todo lo posible, aceptando reset de BBDD local al tratarse de entorno de desarrollo y pruebas.

## Contexto

- Fase Documento 30: Fase 2, modelo de datos y Flyway.
- Backlog Documento 31: roadmap `16`, decision de estrategia Flyway.
- Se permite resetear la BBDD local porque no hay requisito de conservar datos de desarrollo.

## Archivos modificados

- `backend/src/main/resources/db/migration/V1__create_mvp_schema.sql`
- `backend/src/main/resources/db/migration/V2__seed_initial_data.sql`
- `backend/src/main/resources/db/migration/V3..V26__*.sql`
- `backend/pom.xml`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-prod.yml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Consolidar el esquema final en `V1__create_mvp_schema.sql`.
- Consolidar semillas iniciales en `V2__seed_initial_data.sql`.
- Eliminar migraciones incrementales `V3` a `V26`.
- No trasladar `UPDATE` correctivos de datos historicos a la nueva linea base cuando no aplican a una BBDD limpia.
- Documentar que esta reconsolidacion exige reset de BBDD o volumen PostgreSQL por cambio de checksums Flyway.

## Pruebas y verificaciones

- Reset BBDD local: recreado solo el volumen Docker `database_postgres_data`, manteniendo `database_n8n_data`.
- Backend completo: `mvnw.cmd test` OK, 334 tests, 0 fallos, 0 errores.
- Reset final post-tests: recreado de nuevo `database_postgres_data` para dejar la BBDD sin datos de prueba.
- Flyway limpio final: `flyway_schema_history` contiene solo `V1 create mvp schema` y `V2 seed initial data`, ambas `success=true`.
- Semillas finales verificadas: 3 usuarios, 54 fuentes RSS, 3 workflows y 4 prompts IA.
