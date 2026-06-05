# Sprint 2 T2.2 - Entidad de dominio Source

## Fecha

2026-06-06

## Objetivo

Crear la entidad de dominio `Source` dentro del modulo `source`, respetando DDD, Clean Architecture y la Fase 3 del MVP.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 2 - Modulo Source.
- T2.2 - Crear entidad dominio `Source`.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 03C - Modelo de Datos MVP Oficial.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/domain/Source.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/domain/SourceTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_2_entidad_dominio_source.md`.

## Decisiones tomadas

- Se creo `Source` en la capa `domain`, sin anotaciones Spring, JPA, HTTP ni DTOs.
- Se usaron los campos `id`, `name`, `url`, `type`, `priority`, `active`, `createdAt` y `updatedAt`.
- Se incorporaron `createdAt` y `updatedAt` por peticion explicita del usuario, alineados con el modelo fisico definitivo del Documento 20.
- Se uso `OffsetDateTime` para los campos de auditoria por coherencia con `TIMESTAMP WITH TIME ZONE`.
- Se implementaron los comportamientos `activate()` y `deactivate()`, actualizando `updatedAt`.
- Se mantuvo el nombre de atributo `type` y `active`, no `sourceType` ni `isActive`, porque el Documento 20 sustituye definiciones fisicas previas y la migracion MVP vigente usa esos nombres.
- No se creo `SourceRepository`, `SourceEntity`, DTOs, casos de uso ni controllers porque pertenecen a tareas posteriores del Sprint 2.
- Se incremento la version backend a `0.0.5-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.2`: entidad de dominio `Source` completada.

No se marco Sprint 2 como completado porque siguen pendientes T2.3 y tareas posteriores.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 5 tests ejecutados, 0 fallos, 0 errores.
- `SourceTest`: 4 tests unitarios de dominio ejecutados correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
