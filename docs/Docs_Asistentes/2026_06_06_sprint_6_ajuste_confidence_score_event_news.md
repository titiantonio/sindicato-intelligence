# Sprint 6 - Ajuste confidence_score en event_news

## Fecha

2026-06-06

## Objetivo

Preparar la trazabilidad de asociaciones IA de WF-03 añadiendo `confidence_score` a la tabla `event_news` mediante una nueva migracion Flyway.

## Contexto

Durante la revision de Sprint 6 se detecto una contradiccion documental: las reglas de negocio y prompts de IA exigen registrar la confianza de la asociacion noticia-evento, pero el modelo fisico final MVP implementado no incluia ese campo en `event_news`.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 04 - Reglas de Negocio.md`.
- `docs/Documentacion Proyecto/Documento 16 - Arquitectura de Eventos Inteligentes.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-flyway-modelo-datos/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `backend/src/main/resources/db/migration/V4__add_event_news_confidence_score.sql`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_ajuste_confidence_score_event_news.md`.

## Decisiones tomadas

- No se modifico `V2__create_mvp_schema.sql`, porque las migraciones ya existentes no deben alterarse.
- Se creo una migracion nueva `V4` para incorporar `confidence_score`.
- `confidence_score` queda nullable para permitir asociaciones no generadas por IA, como la primera noticia de un evento o futuras operaciones manuales.
- Se anadio una constraint para limitar el valor al rango `0..100` cuando exista.
- No se marco T6.7 como completada porque la asociacion noticia-evento aun requiere caso de uso, persistencia y verificacion.

## Documento 31 actualizado

- Se agrego una nota en T6.7 indicando que la migracion `V4` es un ajuste preparatorio para la trazabilidad IA de WF-03.

## Pruebas y verificaciones

- Ejecutado `./mvnw.cmd test` desde `backend`.
- Resultado: `BUILD SUCCESS`.
- Tests: 79 ejecutados, 0 fallos, 0 errores.
- Flyway valido 4 migraciones y aplico `V4` correctamente sobre PostgreSQL local, dejando el esquema en version 4.
