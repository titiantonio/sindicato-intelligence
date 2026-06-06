# Sprint 5 T5.5 - Prompt WF-02

## Fecha

2026-06-06

## Objetivo

Implementar el builder del prompt oficial WF-02 para clasificacion IA de noticias.

## Contexto

Tarea correspondiente al Documento 31, Sprint 5, T5.5.

## Fase MVP relacionada

Documento 30, Fase 6: Clasificacion IA.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPrompt.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilderTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_5_t5_5_prompt_wf_02.md`.

## Decisiones tomadas

- Se uso exclusivamente el prompt oficial WF-02 del Documento 23.
- El builder recibe titulo, resumen y contenido, sustituyendo las variables `{{title}}`, `{{summary}}` y `{{content}}`.
- El prompt queda en Application para ser usado por el caso de uso antes de invocar `AIProvider`.

## Documento 31 actualizado

- `[x] T5.5`.

## Pruebas y verificaciones

- Se añadieron pruebas unitarias para verificar contenido oficial y sustitucion de datos.
- La suite completa se ejecutara al cierre del Sprint 5.
