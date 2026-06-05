# Sprint 2 T2.7 - CreateSourceUseCase

## Fecha

2026-06-06

## Objetivo

Crear `CreateSourceUseCase` para registrar fuentes desde la capa de aplicacion del modulo `source`.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 2 - Modulo Source.
- T2.7 - Crear Use Case `CreateSourceUseCase`.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/application/CreateSourceCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/application/CreateSourceUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/application/CreateSourceUseCaseTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_7_create_source_use_case.md`.

## Decisiones tomadas

- Se creo `CreateSourceCommand` en `source/application` porque el Documento 19 lo define como entrada del caso de uso.
- Se creo `CreateSourceUseCase` en `source/application`, con anotaciones `@Service` y `@Transactional` para orquestacion transaccional.
- El caso de uso usa el puerto de dominio `SourceRepository` y no depende de JPA, entidades de infraestructura ni DTOs REST.
- El caso de uso devuelve `Source` de dominio; la transformacion a `SourceResponse` queda para la API REST de T2.8.
- Se valida que la URL no exista mediante `SourceRepository.findByUrl` antes de guardar.
- Se inicializan `createdAt` y `updatedAt` con el mismo `OffsetDateTime` al crear una fuente.
- Se incremento la version backend a `0.0.10-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.7`: `CreateSourceUseCase` completado.

No se marco Sprint 2 como completado porque siguen pendientes T2.8 y T2.9.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 18 tests ejecutados, 0 fallos, 0 errores.
- `CreateSourceUseCaseTest`: 3 pruebas unitarias ejecutadas correctamente.
- `CreateSourceRequestTest`: 2 pruebas de validacion ejecutadas correctamente.
- `SourceResponseTest`: 1 prueba de DTO ejecutada correctamente.
- `JpaSourceRepositoryTest`: 3 pruebas de integracion ejecutadas correctamente.
- `SourceEntityTest`: 4 pruebas de mapeo JPA ejecutadas correctamente.
- `SourceTest`: 4 pruebas unitarias de dominio ejecutadas correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
- Consulta posterior a PostgreSQL: no quedaron datos de prueba con URL `https://test.example/%` en `sources`.
