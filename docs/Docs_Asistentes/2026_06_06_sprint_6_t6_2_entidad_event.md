# Sprint 6 T6.2 - Entidad Event

## Fecha

2026-06-06

## Objetivo

Crear la entidad de dominio `Event` como base del agregado principal del sistema.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.2. La Fase 7 del Documento 30 define eventos como el mecanismo para agrupar noticias clasificadas sobre un mismo hecho.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 15 - Plan de Pruebas.md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/domain/Event.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventStatus.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/domain/Importance.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventCategory.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/domain/EventTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_2_entidad_event.md`.

## Decisiones tomadas

- `Event` se implemento en dominio puro, sin dependencias de Spring, JPA, HTTP ni DTOs.
- Se crearon `EventStatus`, `Importance` y `EventCategory` dentro del modulo `event` para evitar acoplar el dominio de eventos al modulo `classification`.
- La entidad incluye `newsIds` para poder cumplir desde dominio la regla de que un evento debe tener al menos una noticia.
- La logica de asociar nuevas noticias, cambiar estados y persistir la relacion se deja para T6.4 y T6.7.

## Documento 31 actualizado

- `[x] T6.2`.

## Pruebas y verificaciones

- Ejecutado `./mvnw.cmd test` desde `backend`.
- Resultado: `BUILD SUCCESS`.
- Tests: 84 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y confirmo el esquema en version 4.
