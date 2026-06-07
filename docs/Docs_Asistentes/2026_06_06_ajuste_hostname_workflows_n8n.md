# Ajuste hostname workflows n8n

## Fecha

2026-06-06

## Objetivo

Ajustar los workflows n8n exportables para usar el hostname real del backend en Docker: `sindicato-backend`.

## Contexto

El workflow de prueba funcional `TEST - Backend Health` usa `http://sindicato-backend:8080/api/v1/health`. Los workflows `WF-01`, `WF-02` y `WF-03` estaban usando `http://backend:8080`, que no coincide con el nombre resoluble en la red de n8n.

## Fase MVP relacionada

- Documento 30, Fase 5: WF-01 Captura Noticias.
- Documento 30, Fase 6: Clasificacion IA.
- Documento 30, Fase 7: Eventos.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `n8n/workflows/wf_02_classify_news.json`.
- `n8n/workflows/wf_03_detect_events.json`.
- `CHANGELOG.md`.
- `docs/Docs_Asistentes/2026_06_06_ajuste_hostname_workflows_n8n.md`.

## Decisiones tomadas

- Se sustituyo `http://backend:8080` por `http://sindicato-backend:8080` en todas las llamadas HTTP de workflows n8n al backend.
- No se modifico `backend/pom.xml` porque no hubo cambios en backend Java ni en el artefacto Maven.

## Pruebas y verificaciones

- Ejecutada validacion JSON con Node sobre `wf_01_capture_news.json`, `wf_02_classify_news.json` y `wf_03_detect_events.json`.
- Resultado: los tres workflows son JSON valido.
- Verificado que no quedan referencias a `http://backend:8080` en `n8n/workflows`.
- Verificado que los workflows usan `http://sindicato-backend:8080` para llamadas HTTP al backend.
