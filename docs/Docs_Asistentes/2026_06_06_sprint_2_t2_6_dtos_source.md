# Sprint 2 T2.6 - DTOs del modulo Source

## Fecha

2026-06-06

## Objetivo

Crear los DTOs `CreateSourceRequest` y `SourceResponse` para la capa API del modulo `source`.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 2 - Modulo Source.
- T2.6 - Crear DTOs `CreateSourceRequest` y `SourceResponse`.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `backend/src/main/java/es/sindicato/intelligence/source/domain/Source.java`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/api/CreateSourceRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/api/SourceResponse.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/api/CreateSourceRequestTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/api/SourceResponseTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_6_dtos_source.md`.

## Decisiones tomadas

- Se ubicaron los DTOs en `source/api`, porque pertenecen a la capa API.
- Se implementaron como `record` para mantener DTOs inmutables y simples.
- `CreateSourceRequest` contiene `name`, `url`, `type`, `priority` y `active`.
- `SourceResponse` contiene `id`, `name`, `url`, `type`, `priority`, `active`, `createdAt` y `updatedAt`.
- Se anadieron validaciones Jakarta en `CreateSourceRequest`: `@NotBlank`, `@Size`, `@URL`, `@NotNull` y `@Min`.
- No se creo controller ni transformacion a comandos porque pertenecen a tareas posteriores.
- Se incremento la version backend a `0.0.9-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.6`: DTOs del modulo Source completados.

No se marco Sprint 2 como completado porque siguen pendientes T2.7 y tareas posteriores.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 15 tests ejecutados, 0 fallos, 0 errores.
- `CreateSourceRequestTest`: 2 pruebas ejecutadas correctamente para validaciones de entrada.
- `SourceResponseTest`: 1 prueba ejecutada correctamente para campos de respuesta.
- `JpaSourceRepositoryTest`: 3 pruebas de integracion ejecutadas correctamente.
- `SourceEntityTest`: 4 pruebas de mapeo JPA ejecutadas correctamente.
- `SourceTest`: 4 pruebas unitarias de dominio ejecutadas correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
- Consulta posterior a PostgreSQL: no quedaron datos de prueba con URL `https://test.example/%` en `sources`.
