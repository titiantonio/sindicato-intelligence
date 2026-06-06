# Sprint 4 T4.4 - Deteccion de duplicados

## Fecha

2026-06-06

## Objetivo

Implementar deteccion de duplicados en la ingesta masiva de noticias de WF-01.

## Contexto

Tarea correspondiente al Documento 31, Sprint 4, T4.4.

## Fase MVP relacionada

Documento 30, Fase 5: WF-01 Captura Noticias.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 04 - Reglas de Negocio.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsHashGenerator.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/CreateNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/CreateNewsUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCaseTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_4_t4_4_deteccion_duplicados.md`.

## Decisiones tomadas

- Se extrajo la logica de hash a `NewsHashGenerator` para reutilizarla en creacion individual y lote.
- Se detectan duplicados en el mismo payload por URL y por hash.
- Se mantiene la deteccion de duplicados contra base de datos (`url` y `hash`) en `CreateNewsUseCase`.
- El comportamiento del endpoint masivo sigue siendo parcial por item.

## Documento 31 actualizado

- `[x] T4.4`.

## Pruebas y verificaciones

- Se añadieron pruebas para duplicados por URL/hash dentro del lote.
- La suite completa se ejecutara al cierre del Sprint 4.
