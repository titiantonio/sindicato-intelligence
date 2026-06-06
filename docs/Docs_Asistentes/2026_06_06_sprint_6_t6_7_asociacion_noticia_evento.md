# Sprint 6 T6.7 - Asociacion noticia-evento

## Fecha

2026-06-06

## Objetivo

Implementar la asociacion entre noticias y eventos para completar WF-03 y cerrar el Sprint 6.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.7. El Documento 30 define como resultado esperado que varias noticias sobre el mismo hecho se agrupen en un unico evento. El Documento 23 establece el umbral IA de asociacion automatica en `confidence >= 85`.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 04 - Reglas de Negocio.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-flyway-modelo-datos/SKILL.md`.
- `skills/sindicato-api-security/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/pom.xml`.
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventRepository.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventResult.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/api/DetectEventRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/api/DetectEventResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/api/EventController.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/EventEntity.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/EventNewsEntity.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/JpaEventRepository.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/api/EventControllerTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_7_asociacion_noticia_evento.md`.

## Decisiones tomadas

- Se implemento `POST /api/v1/events/detect` como endpoint de WF-03 para que n8n delegue en Spring Boot la ejecucion de `MatchEventUseCase`, creacion/asociacion de evento y actualizacion de estado de noticia.
- Se creo `DetectEventUseCase` como orquestador transaccional de la deteccion de eventos.
- Se persistieron `events` y `event_news` con entidades JPA separadas, sin exponer entidades JPA ni dominio en la API.
- `event_news.confidence_score` se actualiza con la confianza devuelta por la comparacion IA.
- Se evita asociar una noticia que no este `CLASSIFIED` o que ya tenga asociacion registrada.
- Tras asociar o crear evento, la noticia se marca como `EVENT_MATCHED`.
- Se actualizo `backend/pom.xml` a `0.0.15-SNAPSHOT` al cierre del Sprint 6.

## Documento 31 actualizado

- `[x] T6.7`.
- `[x] Sprint 6`.

## Pruebas y verificaciones

- Ejecutado `./mvnw.cmd test` desde `backend` antes del cierre documental.
- Resultado: `BUILD SUCCESS`.
- Tests: 94 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y confirmo el esquema en version 4.
