# Sprint 6 T6.5 - Workflow deteccion eventos

## Fecha

2026-06-06

## Objetivo

Crear el workflow n8n exportable `WF-03-Detect-Events` para orquestar la deteccion de eventos a partir de noticias clasificadas.

## Contexto

Tarea correspondiente al Documento 31, Sprint 6, T6.5. El Documento 09 V2.0 define WF-03 como el flujo que lee noticias clasificadas, busca eventos similares, compara y asocia o crea evento. Para mantener la regla arquitectonica de que la logica de negocio reside en Spring Boot, el workflow delega la operacion en backend.

## Fase MVP relacionada

Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `n8n/workflows/wf_03_detect_events.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_6_t6_5_workflow_deteccion_eventos.md`.

## Decisiones tomadas

- El workflow mantiene el patron de `WF-02`: trigger manual, lectura de noticias, filtrado por estado y llamada HTTP al backend.
- Se filtran noticias con `processingStatus === 'CLASSIFIED'`.
- La deteccion se delega en `POST /api/v1/events/detect`, endpoint previsto para implementar la orquestacion de `MatchEventUseCase`, `CreateEventUseCase` y `AddNewsToEventUseCase` en Spring Boot.
- No se implementa logica de matching en n8n; el workflow solo orquesta.

## Documento 31 actualizado

- `[x] T6.5`.

## Pruebas y verificaciones

- Ejecutado `node -e "const fs=require('fs'); JSON.parse(fs.readFileSync('n8n/workflows/wf_03_detect_events.json','utf8')); console.log('wf_03_detect_events.json OK');"`.
- Resultado: `wf_03_detect_events.json OK`.
