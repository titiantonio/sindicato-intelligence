# Ajuste host docker internal workflows

## Fecha

2026-06-06

## Objetivo

Ajustar las URLs de backend en los workflows n8n para que funcionen en desarrollo con n8n ejecutandose en Docker y Spring Boot en la maquina anfitriona.

## Contexto

El usuario indico que en desarrollo las llamadas desde n8n deben usar `http://host.docker.internal:8080`. Los workflows estaban apuntando a `http://sindicato-backend:8080`, hostname valido solo si backend y n8n comparten una red Docker con ese nombre de servicio.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

Tambien afecta a los workflows posteriores ya creados:

- `WF-02-Classify-News`.
- `WF-03-Detect-Events`.

## Archivos modificados

- `n8n/workflows/wf_01_capture_news.json`.
- `n8n/workflows/wf_02_classify_news.json`.
- `n8n/workflows/wf_03_detect_events.json`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_ajuste_host_docker_internal_workflows.md`.

## Decisiones

- Se sustituyeron las llamadas a `http://sindicato-backend:8080` por `http://host.docker.internal:8080`.
- Se mantiene el resto de contratos API sin cambios.
- No se modifico `backend/pom.xml` porque no hubo cambios en backend Java ni migraciones Flyway.

## Pruebas y verificaciones

- Se validaron como JSON los workflows `wf_01_capture_news.json`, `wf_02_classify_news.json` y `wf_03_detect_events.json`.
- Se verifico que no quedan referencias a `http://sindicato-backend:8080`, `http://backend:8080` ni `http://localhost:8080` en `n8n/workflows`.
- Se verifico que existen 6 referencias a `host.docker.internal:8080`, correspondientes a las llamadas HTTP esperadas.

## Resultado

Los workflows n8n quedan preparados para el entorno de desarrollo actual.
