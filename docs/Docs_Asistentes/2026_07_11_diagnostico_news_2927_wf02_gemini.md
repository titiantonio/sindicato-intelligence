# Diagnostico NEWS 2927 en WF-02

## Fecha

2026-07-11

## Objetivo

Diagnosticar por que la noticia `2927` falla repetidamente en `WF-02-Classify-News` con el error `Gemini response does not contain candidates[0].content.parts[0].text`.

## Contexto

- Fase MVP relacionada: Fase 6, clasificacion IA.
- Workflow relacionado: `WF-02-Classify-News`, ejecutado en Spring Boot.
- Caso de uso revisado: `ClassifyNewsUseCase`.
- Adaptador IA revisado: `GeminiAIProvider`.

## Hallazgos

- `news_articles.id=2927` permanece en estado `CAPTURED`, por lo que el scheduler de WF-02 la reintenta.
- La noticia `2927` no pertenece al ambito educativo ni sindical: es una noticia de sucesos sobre Rafa Mir y una condena por violacion.
- La noticia `3065` es el mismo contenido duplicado en otro medio y presenta el mismo patron.
- Ambas noticias contienen terminos de violencia sexual, lo que encaja con una respuesta de Gemini bloqueada o sin texto util antes de generar JSON.
- En `ai_operation_metrics`, `2927` acumula fallos repetidos con `Gemini response does not contain candidates[0].content.parts[0].text`.
- No hay clasificacion persistida para `2927` ni `3065`, por lo que no se marcan como `DISCARDED` y vuelven a entrar en el lote.

## Consultas realizadas

- Consulta de `news_articles` para `id=2927`.
- Consulta de `ai_operation_metrics` para `related_entity_id=2927`.
- Agrupacion de fallos de clasificacion por noticia.
- Comparacion de `news_articles.id in (2927, 3065)`.

## Archivos revisados

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessPendingClassificationsUseCase.java`.

## Decision

No se han modificado datos ni codigo. La causa raiz diagnosticada es que Gemini no devuelve texto para una noticia fuera de ambito con contenido sensible, y el backend no tiene una clasificacion fallback para persistir `OTROS/FUERA_DE_AMBITO` cuando el proveedor no entrega texto.

## Verificaciones

- Verificacion mediante consultas de solo lectura en PostgreSQL local.
- No se ejecutaron tests porque no se modifico codigo de produccion ni de test.
