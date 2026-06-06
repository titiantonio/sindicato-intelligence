# Sprint 3 T3.6 - DTOs del modulo News

## Fecha

2026-06-06

## Objetivo

Crear DTOs de entrada y salida para la API REST del modulo `news`.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.6: DTOs.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/api/CreateNewsRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsResponse.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/api/CreateNewsRequestTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsResponseTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_6_dtos_news.md`.

## Decisiones tomadas

- `CreateNewsRequest` no incluye `hash`; se calculara internamente en backend.
- `capturedAt`, `createdAt`, `updatedAt` y `processingStatus` no se reciben desde API en la creacion; los asignara el caso de uso.
- `NewsResponse` expone los campos persistidos necesarios para trazabilidad del modulo.

## Documento 31 actualizado

- `[x] T3.6`: DTOs completados.

## Pruebas y verificaciones

- Se crearon pruebas unitarias de validacion de request y exposicion de response.
- La suite completa se ejecutara al cierre del Sprint 3.
