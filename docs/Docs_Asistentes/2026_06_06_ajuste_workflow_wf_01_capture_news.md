# Ajuste workflow WF-01 Capture News

## Fecha

2026-06-06

## Objetivo

Crear el workflow n8n exportable `WF-01-Capture-News`, que habia quedado pendiente como archivo dentro de `n8n/workflows` aunque el backend de Sprint 4 ya estaba preparado.

## Contexto

Se reviso que solo existian `wf_02_classify_news.json` y `wf_03_detect_events.json`. La documentacion del Documento 09 V2.0 y el Documento 30 definen WF-01 como captura de noticias mediante ejecucion periodica, lectura de fuentes, RSS/XML, normalizacion y guardado via API.

## Fase MVP relacionada

Documento 30, Fase 5: WF-01 Captura Noticias.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 09 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_ajuste_workflow_wf_01_capture_news.md`.

## Decisiones tomadas

- El workflow usa `Schedule Trigger` cada 30 minutos, segun la documentacion oficial.
- Lee fuentes desde `GET /api/v1/sources` y filtra fuentes activas de tipo `RSS`.
- Lee RSS/XML mediante nodo `Read RSS Feed`.
- Normaliza campos minimos para `CreateNewsRequest`: `sourceId`, `title`, `url`, `summary`, `content` y `publishedAt`.
- Agrupa las noticias en lote y llama a `POST /api/v1/news/bulk`.
- La deduplicacion final, el hash y la persistencia siguen residiendo en Spring Boot, no en n8n.
- No se actualizo `backend/pom.xml` porque no hubo cambios en backend Java ni en el artefacto Maven.

## Documento 31 actualizado

- Se agrego una nota posterior en Sprint 4 indicando la creacion de `n8n/workflows/wf_01_capture_news.json`.

## Pruebas y verificaciones

- Ejecutado `node -e "const fs=require('fs'); JSON.parse(fs.readFileSync('n8n/workflows/wf_01_capture_news.json','utf8')); console.log('wf_01_capture_news.json OK');"`.
- Resultado: `wf_01_capture_news.json OK`.
- No se ejecutan tests backend porque el cambio afecta solo al workflow n8n y documentacion.
