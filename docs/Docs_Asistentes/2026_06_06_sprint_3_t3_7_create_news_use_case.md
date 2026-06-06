# Sprint 3 T3.7 - CreateNewsUseCase

## Fecha

2026-06-06

## Objetivo

Crear el caso de uso `CreateNewsUseCase` para registrar noticias capturadas en el backend.

## Contexto

La tarea seleccionada en el Documento 31 fue Sprint 3, T3.7: `CreateNewsUseCase`.

## Fase MVP relacionada

Documento 30, Fase 4: Modulo News.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/CreateNewsCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/CreateNewsUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/CreateNewsUseCaseTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_3_t3_7_create_news_use_case.md`.

## Decisiones tomadas

- El caso de uso valida que la fuente exista antes de crear la noticia.
- Se rechazan duplicados por `url` y por `hash` antes de persistir.
- El `hash` se calcula internamente con SHA-256 sobre `normalize(title) + "|" + normalize(content o summary) + "|" + publishedAt`.
- La noticia se crea en estado inicial `CAPTURED`.
- Se mantuvo el estilo de excepciones del modulo `source`, usando `IllegalArgumentException` para reglas de rechazo existentes.

## Documento 31 actualizado

- `[x] T3.7`: `CreateNewsUseCase` completado.

## Pruebas y verificaciones

- Se crearon pruebas unitarias con Mockito para creacion correcta, fuente inexistente, duplicado por URL, duplicado por hash y comando nulo.
- La suite completa se ejecutara al cierre del Sprint 3.
