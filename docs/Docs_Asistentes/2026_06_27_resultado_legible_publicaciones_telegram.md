# 2026-06-27 - Resultado legible de publicaciones Telegram

## Fecha

2026-06-27.

## Objetivo

Evitar que el backoffice muestre payloads tecnicos de Telegram como `{"ok":true,"messageId":"459"}` en el resultado de una publicacion.

## Contexto

La pantalla de publicaciones ya tenia trazabilidad completa, pero el resultado operativo seguia mostrando el JSON persistido en `responsePayload`, poco util para revision editorial.

## Fase MVP

Fase 11 del Documento 30, ampliacion posterior de `T11.8 Publicaciones` en el Documento 31.

## Archivos modificados

- `frontend/src/app/features/publications/publication-result.formatter.ts`.
- `frontend/src/app/features/publications/publication-result.formatter.spec.ts`.
- `frontend/src/app/features/publications/publications-page.component.ts`.
- `frontend/src/app/features/publications/publication-detail-page.component.ts`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones

- Se mantiene el payload tecnico persistido sin cambios.
- La traduccion a texto operativo se realiza en Angular para listado y detalle de publicaciones.
- Se soportan `messageId`, `message_id`, `result.message_id`, payloads de error con `description` y fallback por `externalId`.

## Pruebas o verificaciones

- `npm test -- --watch=false --browsers=ChromeHeadless`: 136 tests correctos.
- `npm run build`: correcto con avisos existentes de presupuesto Angular.
