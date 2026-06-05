# Sprint 2 T2.3 - SourceRepository de dominio

## Fecha

2026-06-06

## Objetivo

Crear `SourceRepository` como interfaz de dominio del modulo `source`, siguiendo DDD y Clean Architecture.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 2 - Modulo Source.
- T2.3 - Crear `SourceRepository` como interface dominio.

La tarea corresponde al Documento 30, Fase 3: Modulo Sources.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Fase MVP relacionada

Documento 30, Fase 3: Modulo Sources.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/source/domain/SourceRepository.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_2_t2_3_source_repository.md`.

## Decisiones tomadas

- Se creo `SourceRepository` en `source/domain`, sin anotaciones Spring, JPA ni dependencias de infraestructura.
- Se definio como puerto de persistencia para los casos de uso del modulo `source`.
- Se incluyeron operaciones `save`, `findById`, `findByUrl` y `findAll` para cubrir creacion, actualizacion, consulta, control de URL unica y listado de fuentes.
- No se creo implementacion JPA porque pertenece a T2.5.
- No se creo `SourceEntity` porque pertenece a T2.4.
- Se incremento la version backend a `0.0.6-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T2.3`: `SourceRepository` de dominio completado.

No se marco Sprint 2 como completado porque siguen pendientes T2.4 y tareas posteriores.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`.
- 5 tests ejecutados, 0 fallos, 0 errores.
- Compilacion de 9 archivos fuente, incluyendo `SourceRepository`.
- `SourceTest`: 4 tests unitarios de dominio ejecutados correctamente.
- `IntelligenceApplicationTests`: contexto Spring cargado correctamente.
- Flyway valido 3 migraciones y confirmo que el esquema `public` esta actualizado en version 3.
