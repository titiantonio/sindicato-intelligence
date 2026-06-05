# Sprint 2 T2.4 - SourceEntity JPA

## Fecha

2026-06-06

## Objetivo

Crear `SourceEntity` como entidad JPA del modulo `source`, mapeada a la tabla `sources` definida por Flyway.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 2 - Modulo Source.
- T2.4 - Crear entidad JPA `SourceEntity`.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `backend/src/main/resources/db/migration/V2__create_mvp_schema.sql`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/infrastructure/SourceEntity.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/infrastructure/SourceEntityTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_4_source_entity.md`.

## Decisiones tomadas

- Se creo `SourceEntity` en `source/infrastructure`, porque JPA pertenece a infraestructura.
- Se mapeo a la tabla `sources` y a las columnas `id`, `name`, `url`, `type`, `priority`, `active`, `created_at` y `updated_at`.
- Se uso `GenerationType.IDENTITY` por coherencia con `BIGSERIAL` en PostgreSQL.
- Se uso `OffsetDateTime` para `createdAt` y `updatedAt` por coherencia con `TIMESTAMP WITH TIME ZONE`.
- Se anadio constructor sin argumentos protegido para JPA.
- No se creo `JpaSourceRepository` porque pertenece a T2.5.
- No se creo mapper ni adaptador de repositorio porque no esta solicitado en T2.4.
- Se incremento la version backend a `0.0.7-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.4`: entidad JPA `SourceEntity` completada.

No se marco Sprint 2 como completado porque siguen pendientes T2.5 y tareas posteriores.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 9 tests ejecutados, 0 fallos, 0 errores.
- `SourceEntityTest`: 4 tests de mapeo JPA ejecutados correctamente.
- `SourceTest`: 4 tests unitarios de dominio ejecutados correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
