# Sprint 4 T4.1 - Modificar flujo de captura

## Fecha

2026-06-06

## Objetivo

Preparar la capa de aplicacion para que la captura WF-01 entre por API en lote, evitando escritura directa desde n8n a base de datos.

## Contexto

Tarea correspondiente al Documento 31, Sprint 4, T4.1.

## Fase MVP relacionada

Documento 30, Fase 5: WF-01 Captura Noticias.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchItemResult.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchResult.java`.
- `backend/src/main/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/news/application/IngestNewsBatchUseCaseTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_4_t4_1_modificar_flujo_captura.md`.

## Decisiones tomadas

- Se introdujo un caso de uso especifico de ingesta por lote para WF-01.
- Se aplico procesamiento parcial por item para no abortar todo el lote ante errores puntuales.
- El controller seguira delegando la orquestacion al caso de uso, sin logica de negocio.

## Documento 31 actualizado

- `[x] T4.1`.

## Pruebas y verificaciones

- Se añadieron pruebas unitarias de `IngestNewsBatchUseCase`.
- La suite completa se ejecutara al cierre del Sprint 4.
