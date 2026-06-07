# Correccion WF-01 items n8n

## Fecha

2026-06-06

## Objetivo

Corregir el error del workflow `WF-01-Capture-News` en el nodo `Filter Active RSS Sources`, donde n8n marcaba `Cannot find name 'items'`.

## Contexto

El primer `HTTP Request` devuelve correctamente las fuentes desde `GET /api/v1/sources`, pero el siguiente Code node usaba la variable `items`. En versiones recientes de n8n el editor recomienda y tipa correctamente `$input.all()` para acceder a los datos de entrada.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_correccion_wf_01_items_n8n.md`.

## Decisiones

- Se cambio `items.filter(...)` por `$input.all()` en `Filter Active RSS Sources`.
- El filtro ahora soporta tanto una salida del HTTP Request dividida en varios items como una salida unica cuyo `json` sea un array.
- Se cambio el nodo `Build Batch` para iterar con `$input.all()` en lugar de `items`.
- Se mantiene el contrato final de `POST /api/v1/news/bulk` sin cambios.
- No se modifico `backend/pom.xml` porque no hubo cambios en backend Java ni migraciones Flyway.

## Pruebas y verificaciones

- Ejecutado validador JSON del workflow con Node.
- Verificado que ningun Code node de `wf_01_capture_news.json` usa ya la variable `items`.
- Resultado: `wf_01_capture_news.json OK, no items variable`.

## Resultado

El workflow queda corregido para evitar el error de n8n `Cannot find name 'items'` en los Code nodes.
