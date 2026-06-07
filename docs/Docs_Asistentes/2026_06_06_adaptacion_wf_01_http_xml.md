# Adaptacion WF-01 HTTP XML

## Fecha

2026-06-06

## Objetivo

Adaptar `n8n/workflows/wf_01_capture_news.json` usando la parte util del workflow historico `n8n/workflows/01-ingesta-noticias.json`, sin cargar todavia las fuentes historicas en base de datos.

## Contexto

El workflow oficial `WF-01-Capture-News` ya estaba creado y llamaba a `POST /api/v1/news/bulk`, pero usaba el nodo nativo `Read RSS Feed`. El workflow historico tenia una captura mas robusta para fuentes que requieren descarga HTTP con cabeceras concretas y parseo XML posterior.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Documentacion revisada

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Tecnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_adaptacion_wf_01_http_xml.md`.

## Decisiones

- Se sustituyo el nodo `Read RSS Feed` por `HTTP Request` para descargar cada feed como texto.
- El `HTTP Request` usa los headers `User-Agent: Wget/1.21.1` y `Accept: */*`.
- Se anadio un filtro para descartar respuestas vacias o con error antes del parseo XML.
- Se anadio un nodo `XML` para convertir RSS/Atom a JSON antes de normalizar.
- El codigo JS normaliza Atom de Junta Andalucia y RSS estandar al contrato de `CreateNewsRequest`: `sourceId`, `title`, `url`, `summary`, `content` y `publishedAt`.
- El workflow mantiene la llamada final a `POST http://sindicato-backend:8080/api/v1/news/bulk`.
- No se anadieron fuentes por Flyway porque el usuario indico que deben revisarse aparte por errores en algunas fuentes.
- No se modifico `backend/pom.xml` porque no hubo cambios en backend Java ni migraciones Flyway.

## Documento 31 actualizado

- Se anadio una nota posterior en el bloque `WF-01 Captura Noticias` indicando el ajuste de captura HTTP/XML y que las fuentes quedan fuera de esta intervencion.

## Pruebas y verificaciones

- Ejecutado `node -e "const fs=require('fs'); JSON.parse(fs.readFileSync('n8n/workflows/wf_01_capture_news.json','utf8')); console.log('wf_01_capture_news.json OK');"`.
- Resultado: `wf_01_capture_news.json OK`.
- No se ejecutaron tests backend porque el cambio afecta solo al workflow n8n y documentacion.
