# Sprint 6 T6.6 - Integracion IA agrupacion

## Fecha

2026-06-06

## Objetivo

Integrar la comparacion IA para determinar si una noticia clasificada pertenece a un evento existente.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.6. El Documento 23 define el prompt oficial WF-03 y el umbral MVP para asociacion automatica de eventos (`confidence >= 85`).

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchCandidate.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchingAIRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchingAIResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchingAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchPrompt.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchPromptBuilder.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/DeterministicEventMatchingAIProvider.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/application/EventMatchPromptBuilderTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/infrastructure/DeterministicEventMatchingAIProviderTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_6_integracion_ia_agrupacion.md`.

## Decisiones tomadas

- Se creo un puerto especifico `EventMatchingAIProvider` dentro del modulo `event` para no acoplar eventos al modulo `classification`.
- Se implemento `EventMatchPromptBuilder` con el prompt oficial WF-03 del Documento 23.
- Se creo `DeterministicEventMatchingAIProvider` como adaptador simulado/determinista para el MVP tecnico, siguiendo el criterio usado previamente en clasificacion.
- El umbral de asociacion automatica se fija en `confidence >= 85`, conforme al Documento 23.
- La respuesta IA valida que `confidence` este entre `0` y `100`.

## Documento 31 actualizado

- `[x] T6.6`.

## Pruebas y verificaciones

- Ejecutado `./mvnw.cmd test` desde `backend`.
- Resultado: `BUILD SUCCESS`.
- Tests: 92 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y confirmo el esquema en version 4.
