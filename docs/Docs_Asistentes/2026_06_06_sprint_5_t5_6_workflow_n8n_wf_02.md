# Sprint 5 T5.6 - Workflow n8n WF-02

## Fecha

2026-06-06

## Objetivo

Crear el workflow n8n exportable `WF-02-Classify-News` para orquestar la clasificacion de noticias capturadas.

## Contexto

Tarea correspondiente al Documento 31, Sprint 5, T5.6.

## Fase MVP relacionada

Documento 30, Fase 6: Clasificacion IA.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos modificados

- `n8n/workflows/wf_02_classify_news.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_5_t5_6_workflow_n8n_wf_02.md`.

## Decisiones tomadas

- El workflow n8n orquesta, pero no contiene reglas de negocio.
- El flujo lee noticias desde la API, filtra `CAPTURED` y llama a `POST /api/v1/classifications/classify`.
- El endpoint de clasificacion se implementara en T5.7.

## Documento 31 actualizado

- `[x] T5.6`.

## Pruebas y verificaciones

- Validacion documental del workflow exportable.
- La integracion API se verificara con pruebas backend en T5.7/T5.8.
