# Sprint 5 T5.8 - Actualizar estado noticia

## Fecha

2026-06-06

## Objetivo

Actualizar el estado de la noticia a `CLASSIFIED` al completar correctamente la clasificacion IA.

## Contexto

Tarea correspondiente al Documento 31, Sprint 5, T5.8.

## Fase MVP relacionada

Documento 30, Fase 6: Clasificacion IA.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsArticle.java`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/api/ClassificationControllerTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_5_t5_8_actualizar_estado_noticia.md`.

## Decisiones tomadas

- Se actualiza el estado mediante el metodo de dominio `NewsArticle.markClassified()`.
- La persistencia de la clasificacion y el cambio de estado ocurren dentro del mismo caso de uso transaccional.
- El workflow WF-02 puede usar el estado `CLASSIFIED` como resultado operativo.

## Documento 31 actualizado

- `[x] T5.8`.

## Pruebas y verificaciones

- Se actualizaron pruebas de caso de uso y API para verificar el estado `CLASSIFIED`.
- La suite completa se ejecutara al cierre del Sprint 5.
