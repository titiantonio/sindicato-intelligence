# Sprint 5 T5.4 - AIProvider determinista

## Fecha

2026-06-06

## Objetivo

Integrar un puerto `AIProvider` y un adaptador determinista para clasificacion IA en MVP tecnico.

## Contexto

Tarea correspondiente al Documento 31, Sprint 5, T5.4.

## Fase MVP relacionada

Documento 30, Fase 6: Clasificacion IA.

## Documentacion revisada antes de implementar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 10 - Diseño de IA y Prompts.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/AIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassificationAIRequest.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassificationAIResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/DeterministicAIProvider.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/DeterministicAIProviderTest.java`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_06_sprint_5_t5_4_ai_provider_determinista.md`.

## Decisiones tomadas

- Se implemento un adaptador determinista/simulado, segun decision del usuario, sin dependencias externas ni claves API.
- `AIProvider` queda como puerto para sustituir posteriormente por OpenAI/Ollama sin tocar dominio ni casos de uso.
- La clasificacion determinista usa reglas simples sobre texto normalizado y devuelve categorias oficiales.

## Documento 31 actualizado

- `[x] T5.4`.

## Pruebas y verificaciones

- Se añadieron pruebas unitarias del proveedor determinista para SIPRI, oposiciones, retribuciones y OTROS.
- La suite completa se ejecutara al cierre del Sprint 5.
