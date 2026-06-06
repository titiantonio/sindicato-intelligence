# Sprint 6 T6.3 - EventRepository

## Fecha

2026-06-06

## Objetivo

Crear el puerto de dominio `EventRepository` para permitir persistencia y consulta de eventos desde casos de uso sin acoplar el dominio a JPA o Spring.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.3. El Documento 19 define casos de uso de eventos como consulta, busqueda, matching y asociacion de noticias, que necesitan un puerto de repositorio.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventRepository.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_3_event_repository.md`.

## Decisiones tomadas

- `EventRepository` se ubico en dominio como puerto, siguiendo los patrones ya usados en `SourceRepository`, `NewsRepository` y `NewsClassificationRepository`.
- El puerto permite guardar, consultar por id, listar y buscar por estado, categoria e importancia.
- Se incluyo `findByStatusIn` para preparar la busqueda de eventos activos en WF-03 sin introducir todavia persistencia JPA.

## Documento 31 actualizado

- `[x] T6.3`.

## Pruebas y verificaciones

- Ejecutado `./mvnw.cmd test` desde `backend`.
- Resultado: `BUILD SUCCESS`.
- Tests: 84 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y confirmo el esquema en version 4.
