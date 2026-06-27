# Documentación del flujo completo WF-01 a WF-06

## Fecha

2026-06-27

## Objetivo

Crear un documento técnico y operativo que explique el flujo completo desde la captura RSS en `WF-01` de n8n hasta la publicación en Telegram mediante `WF-06` en Spring Boot.

## Contexto

El proyecto mantiene n8n solo para `WF-01-Capture-News`. Las automatizaciones `WF-02` a `WF-06` residen en Spring Boot, con PostgreSQL como persistencia y Gemini/deterministic providers como soporte IA según configuración.

Se revisaron las instrucciones de `docs/00-agent-context.md`, las skills de arquitectura MVP, workflows IA/n8n y documentación, el workflow real `n8n/workflows/wf_01_capture_news.json`, los builders/providers IA y los casos de uso de automatización, contenido y publicación.

## Fase MVP

Intervención documental transversal sobre las fases ya implementadas:

- Fase 5: `WF-01` captura n8n.
- Fase 6: clasificación IA.
- Fase 7: eventos.
- Fase 8: análisis IA.
- Fase 9: contenido.
- Fase 10: publicación Telegram.
- Fase 12: automatizaciones internas, configuración y observabilidad IA.

## Archivos modificados

- `docs/Documentacion Proyecto/2026_06_27_flujo_completo_wf_01_wf_06.md`
- `docs/Docs_Asistentes/2026_06_27_documentacion_flujo_completo_wf_01_wf_06.md`

## Decisiones

- Se creó un documento principal independiente con diagramas Mermaid, entradas/salidas por workflow, prompts completos, contratos JSON, persistencia y ejemplo real de BBDD.
- Se incluyeron los prompts literales desde el código actual, manteniendo incluso textos con mojibake cuando aparecen en los builders reales.
- Se usó como ejemplo real el caso `publication_id=158`, `content_id=333`, `event_id=184`, con publicación Telegram `messageId=459`.
- No se actualizó `Documento 31` porque la tarea solicitada es documental y no implementa una subtarea funcional nueva.
- No se actualizó `pom.xml` ni `CHANGELOG.md` porque no hubo cambios de código.

## Pruebas o verificaciones

- Se consultó PostgreSQL local en modo solo lectura para validar el ejemplo real:
  - `publications`
  - `generated_content`
  - `events`
  - `event_news`
  - `news_articles`
  - `news_classifications`
  - `event_ai_analysis`
  - `ai_operation_metrics`
- Se verificó que existía un flujo publicado completo con `publication_id=158`.
- Se validó visualmente la estructura Markdown, bloques JSON y diagramas Mermaid.
- No se ejecutaron tests de backend porque la intervención solo modifica documentación Markdown.
