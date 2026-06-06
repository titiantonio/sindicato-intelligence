# Sprint 4 T4.2 - Endpoint de ingestión masiva

## Fecha

2026-06-06

## Objetivo

Crear el endpoint de ingestión masiva `POST /api/v1/news/bulk` para que WF-01 envíe lotes de noticias desde n8n.

## Contexto

Tarea correspondiente al Documento 31, Sprint 4, T4.2.

## Fase MVP relacionada

Documento 30, Fase 5: WF-01 Captura Noticias.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/api/IngestNewsBatchItemResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/api/IngestNewsBatchResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsController.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_4_t4_2_endpoint_ingestion_bulk.md`.

## Decisiones tomadas

- Se expuso `POST /api/v1/news/bulk` en el modulo `news`.
- El endpoint delega en `IngestNewsBatchUseCase` y no contiene logica de negocio.
- La respuesta incluye resumen de lote y detalle por item para trazabilidad operativa en n8n.
- Se mantuvo el endpoint individual `POST /api/v1/news` para compatibilidad.

## Documento 31 actualizado

- `[x] T4.2`.

## Pruebas y verificaciones

- Las pruebas de integración del endpoint masivo se implementaran en T4.5.
