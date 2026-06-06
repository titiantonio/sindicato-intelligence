# Sprint 4 T4.3 - Normalizacion RSS

## Fecha

2026-06-06

## Objetivo

Implementar la normalizacion de datos RSS antes de registrar noticias capturadas en el flujo masivo de WF-01.

## Contexto

Tarea correspondiente al Documento 31, Sprint 4, T4.3.

## Fase MVP relacionada

Documento 30, Fase 5: WF-01 Captura Noticias.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsCaptureNormalizer.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/NewsCaptureNormalizerTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_4_t4_3_normalizacion_rss.md`.

## Decisiones tomadas

- Se centralizo la normalizacion de captura en `NewsCaptureNormalizer`.
- Se normalizan espacios y extremos de `title`, `url`, `summary` y `content`.
- Campos opcionales (`summary`, `content`) quedan en `null` si llegan vacios tras normalizacion.

## Documento 31 actualizado

- `[x] T4.3`.

## Pruebas y verificaciones

- Se añadieron pruebas unitarias para normalizador y su uso en ingesta por lote.
- La suite completa se ejecutara al cierre del Sprint 4.
