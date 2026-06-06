# Sprint 3 T3.8 - GetNewsUseCase y ListNewsUseCase

## Fecha

2026-06-06

## Objetivo

Crear casos de uso de consulta para obtener una noticia por identificador y listar noticias.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.8: `GetNewsUseCase`.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/NewsNotFoundException.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/GetNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/ListNewsUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/GetNewsUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/ListNewsUseCaseTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_8_get_list_news_use_cases.md`.

## Decisiones tomadas

- Se creo `GetNewsUseCase` como consulta por `id` con `NewsNotFoundException` si no existe.
- Se agrego `ListNewsUseCase` para soportar el endpoint `GET /api/v1/news` previsto en T3.9.
- No se implementaron filtros todavia porque T3.8 del Documento 31 solo exige la consulta simple y el MVP de Sprint 3 lista noticias sin paginacion.

## Documento 31 actualizado

- `[x] T3.8`: casos de uso de consulta completados.

## Pruebas y verificaciones

- Se crearon pruebas unitarias de `GetNewsUseCase` y `ListNewsUseCase`.
- La suite completa se ejecutara al cierre del Sprint 3.
