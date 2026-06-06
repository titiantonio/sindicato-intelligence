# Sprint 3 T3.9 - API REST News

## Fecha

2026-06-06

## Objetivo

Exponer endpoints REST para crear, listar y consultar noticias del modulo `news`.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.9: API REST.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/api/NewsController.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/api/NewsControllerTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_9_api_rest_news.md`.

## Decisiones tomadas

- Se expusieron `POST /api/v1/news`, `GET /api/v1/news` y `GET /api/v1/news/{id}`.
- El controller solo valida, transforma DTOs y delega en casos de uso.
- No se devuelven entidades JPA ni entidades de dominio desde la API.
- Se mantuvo el tratamiento de errores local usado por el modulo `source`.

## Documento 31 actualizado

- `[x] T3.9`: API REST completada.

## Pruebas y verificaciones

- Se crearon pruebas MockMvc para crear, listar, consultar por id, validar request invalido y devolver 404 ante noticia inexistente.
- La suite completa se ejecutara al cierre del Sprint 3.
