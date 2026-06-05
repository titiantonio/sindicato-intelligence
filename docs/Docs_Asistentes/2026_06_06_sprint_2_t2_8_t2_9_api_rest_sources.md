# Sprint 2 T2.8 y T2.9 - API REST del modulo Source

## Fecha

2026-06-06

## Objetivo

Implementar la API REST del modulo `source` y completar las pruebas necesarias para cerrar el Sprint 2.

## Contexto

Las tareas seleccionadas en el Documento 31 fueron:

- Sprint 2 - Modulo Source.
- T2.8 - Crear API REST.
- T2.9 - Crear tests.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 13 - Seguridad y Roles.md`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/application/ListSourcesUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/application/SourceNotFoundException.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/application/UpdateSourceCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/application/UpdateSourceUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/api/UpdateSourceRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/source/api/SourceController.java`.
- `backend/src/test/java/es/sindicato/intelligence/source/api/SourceControllerTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_8_t2_9_api_rest_sources.md`.

## Decisiones tomadas

- Se implementaron los endpoints oficiales `GET /api/v1/sources`, `POST /api/v1/sources` y `PUT /api/v1/sources/{id}`.
- Se anadieron `ListSourcesUseCase` y `UpdateSourceUseCase` porque el Documento 30 los define para la Fase 3 y son necesarios para los endpoints GET y PUT.
- Se mantuvo la logica de negocio en Application y Domain; el controller solo valida, transforma y devuelve HTTP.
- Se creo `UpdateSourceRequest` porque PUT requiere payload propio.
- Se creo `SourceNotFoundException` para separar el caso de fuente inexistente de errores HTTP.
- Se devolvio `201 Created` en POST y `200 OK` en GET/PUT.
- Se anadieron manejadores locales para devolver `404` en fuente inexistente y `400` en errores de negocio como URL duplicada.
- No se implemento JWT real porque la seguridad MVP aun no esta desarrollada y la configuracion actual permite las peticiones.
- Se incremento la version backend a `0.0.11-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.8`: API REST del modulo Source completada.
- `[x] T2.9`: tests del modulo Source completados.
- `[x] Sprint 2`: marcado como completado porque todas sus tareas verificables estan finalizadas.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 23 tests ejecutados, 0 fallos, 0 errores.
- `SourceControllerTest`: 5 pruebas REST ejecutadas correctamente para crear, listar, actualizar, validar request invalida y devolver 404 en fuente inexistente.
- `CreateSourceUseCaseTest`: 3 pruebas unitarias ejecutadas correctamente.
- `CreateSourceRequestTest`: 2 pruebas de validacion ejecutadas correctamente.
- `SourceResponseTest`: 1 prueba de DTO ejecutada correctamente.
- `JpaSourceRepositoryTest`: 3 pruebas de integracion ejecutadas correctamente.
- `SourceEntityTest`: 4 pruebas de mapeo JPA ejecutadas correctamente.
- `SourceTest`: 4 pruebas unitarias de dominio ejecutadas correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
- Consulta posterior a PostgreSQL: no quedaron datos de prueba con URL `https://test.example/%` en `sources`.

Incidencia corregida durante la verificacion:

- Un test de actualizacion creo inicialmente una fuente con `createdAt` futuro respecto a `OffsetDateTime.now()`, provocando rechazo de dominio porque `updatedAt` no puede ser anterior a `createdAt`.
- Se corrigio el dato de prueba para usar un `createdAt` pasado y la suite completa paso correctamente.
