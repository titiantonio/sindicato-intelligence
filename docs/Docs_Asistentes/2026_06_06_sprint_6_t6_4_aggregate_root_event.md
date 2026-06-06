# Sprint 6 T6.4 - Aggregate Root Event

## Fecha

2026-06-06

## Objetivo

Convertir `Event` en un aggregate root operativo con comportamiento de negocio para gestionar noticias asociadas y ciclo de vida.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.4. La documentacion de dominio define `Event` como agregado principal del sistema y exige que un evento tenga al menos una noticia y un unico estado.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 04 - Reglas de Negocio.md`.
- `docs/Documentacion Proyecto/Documento 16 - Arquitectura de Eventos Inteligentes.md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/domain/Event.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/domain/EventTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_4_aggregate_root_event.md`.

## Decisiones tomadas

- `Event` mantiene internamente una coleccion mutable protegida de `newsIds` y expone copias defensivas.
- Se agrego `addNews` para asociar noticias solo cuando el evento esta activo (`OPEN` o `MONITORING`).
- Se agrego `removeNews` manteniendo la regla de que el evento no puede quedarse sin noticias.
- Se agregaron transiciones de estado `markMonitoring`, `close`, `reopen` y `archive`.
- Los cambios de asociacion o estado actualizan `lastUpdatedAt` y `updatedAt` manteniendo coherencia temporal.

## Documento 31 actualizado

- `[x] T6.4`.

## Pruebas y verificaciones

- Primer intento de `./mvnw.cmd test` interrumpido por timeout de herramienta a los 120 segundos, sin fallo reportado por Maven.
- Reejecutado `./mvnw.cmd test` desde `backend` con mayor margen.
- Resultado: `BUILD SUCCESS`.
- Tests: 89 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y confirmo el esquema en version 4.
