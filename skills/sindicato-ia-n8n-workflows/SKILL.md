---
name: sindicato-ia-n8n-workflows
description: Usar para trabajar con prompts IA oficiales, n8n, workflows WF-01 a WF-06, clasificacion, agrupacion de eventos, analisis, generacion de contenido y publicacion Telegram. Activa esta skill ante cualquier tarea sobre IA, prompts, automatizaciones o Telegram.
---

# Sindicato IA n8n Workflows

## Proposito

Asegura que n8n orqueste procesos y que Spring Boot mantenga las reglas de negocio.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.

## Workflows oficiales

- `WF-01-Capture-News`: captura noticias y llama a `CreateNewsUseCase`.
- `WF-02-Classify-News`: clasifica noticias y llama a `ClassifyNewsUseCase`.
- `WF-03-Detect-Events`: detecta eventos con `MatchEventUseCase`, `CreateEventUseCase` y `AddNewsToEventUseCase`.
- `WF-04-Analysis`: genera analisis con `GenerateAnalysisUseCase`.
- `WF-05-Generate-Content`: genera contenido con `GenerateContentUseCase`.
- `WF-06-Publish-Telegram`: publica con `PublishContentUseCase`.

## Reglas IA

- Usar exclusivamente prompts oficiales del Documento 23.
- No inventar informacion.
- Basar conclusiones solo en datos proporcionados.
- Responder en JSON cuando el workflow lo requiera.
- La IA apoya decisiones; no reemplaza reglas de dominio.
