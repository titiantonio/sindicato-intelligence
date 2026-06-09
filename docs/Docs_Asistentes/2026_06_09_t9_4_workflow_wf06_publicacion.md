# T9.4 Workflow WF-06 publicacion

## Fecha

2026-06-09

## Objetivo

Crear el workflow n8n de publicacion Telegram del Sprint 9.

## Contexto

Se revisaron el Documento 31 para T9.4, el Documento 09 V2.0 para WF-06 y reintentos Telegram, el Documento 12 para el endpoint de publicaciones y el workflow WF-05 existente como referencia de estilo.

## Fase MVP

Fase 10: Publicacion.

## Archivos modificados

- `n8n/workflows/wf_06_publish_telegram.json`
- `CHANGELOG.md`

## Decisiones

- El workflow recibe `contentId` desde webhook o ejecucion manual.
- n8n no valida reglas de negocio; Spring Boot valida contenido aprobado, publica y registra en `publications`.
- El nodo HTTP de publicacion queda con 3 reintentos para alinear el tratamiento de errores Telegram del Documento 09.

## Pruebas o verificaciones

- Verificado con `node -e "JSON.parse(require('fs').readFileSync('n8n/workflows/wf_06_publish_telegram.json','utf8')); console.log('wf_06_publish_telegram.json OK')"`.
