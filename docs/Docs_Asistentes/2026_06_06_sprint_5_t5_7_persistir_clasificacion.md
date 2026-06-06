# Sprint 5 T5.7 - Persistir clasificacion

## Fecha

2026-06-06

## Objetivo

Persistir clasificaciones IA en `news_classifications` e incorporar el caso de uso y endpoint de clasificacion para WF-02.

## Contexto

Tarea correspondiente al Documento 31, Sprint 5, T5.7.

## Fase MVP relacionada

Documento 30, Fase 6: Clasificacion IA.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 15 - Plan de Pruebas.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/NewsClassificationEntity.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/JpaNewsClassificationRepository.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsCommand.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/api/ClassifyNewsRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/api/NewsClassificationResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/api/ClassificationController.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/JpaNewsClassificationRepositoryTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/api/ClassificationControllerTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_5_t5_7_persistir_clasificacion.md`.

## Decisiones tomadas

- Se mapeo `news_classifications` con JPA respetando columnas e indices del modelo fisico.
- `keywords` y `entities` se persisten como JSONB mediante Hibernate JSON.
- `ClassifyNewsUseCase` invoca `AIProvider`, crea `NewsClassification` y evita doble clasificacion por `newsId`.
- Se expuso `POST /api/v1/classifications/classify` para ser invocado por n8n WF-02.
- El cambio de estado de noticia queda para T5.8, segun secuencia del Documento 31.

## Documento 31 actualizado

- `[x] T5.7`.

## Pruebas y verificaciones

- Se añadieron pruebas unitarias de caso de uso y pruebas de integracion de repositorio/API.
- La suite completa se ejecutara al cierre del Sprint 5.
