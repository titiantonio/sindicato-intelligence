# Correccion Normalize RSS Items

## Fecha

2026-06-06

## Objetivo

Corregir el nodo `Normalize RSS Items` de `WF-01-Capture-News`, que recibia items desde el nodo XML pero no generaba salida.

## Contexto

El workflow ya obtenia fuentes y el nodo XML entregaba 23 items al normalizador. El problema estaba en que el codigo JS buscaba estructuras demasiado concretas, principalmente `feed.entry` para Atom y `rss.channel.item` para RSS. n8n puede envolver el XML parseado en rutas como `data`, `root`, `body`, `feed`, `rss` o `channel`, e incluso usar arrays intermedios.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_correccion_normalize_rss_items.md`.

## Decisiones

- Se anadio la funcion `firstWith` para localizar la primera estructura que contenga `entry` o `item` aunque venga envuelta en distintos niveles.
- Atom se detecta buscando `entry` en candidatos como `feed`, `root.feed`, `body.feed`, `root`, `body` o el objeto completo.
- RSS se detecta buscando `item` en candidatos como `rss.channel`, `channel`, `root.rss.channel`, `root.channel`, `body.rss.channel`, `body.channel` o el objeto completo.
- Se mantiene la salida al contrato del endpoint `POST /api/v1/news/bulk`: `sourceId`, `title`, `url`, `summary`, `content` y `publishedAt`.
- No se modifico `backend/pom.xml` porque no hubo cambios en backend Java ni migraciones Flyway.

## Pruebas y verificaciones

- Validado `wf_01_capture_news.json` como JSON correcto.
- Validada la sintaxis JavaScript del nodo `Normalize RSS Items` mediante `new Function`.
- Resultado: `wf_01_capture_news.json OK, Normalize RSS Items JS syntax OK`.

## Resultado

El normalizador queda preparado para producir noticias desde estructuras Atom/RSS parseadas por n8n aunque el XML no tenga exactamente la forma inicialmente esperada.
