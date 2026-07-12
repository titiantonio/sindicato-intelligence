# Reintento reducido WF-03 para Gemini sin texto

## Fecha

2026-07-12

## Objetivo

Corregir el fallo de `WF-03` sobre la misma noticia educativa `newsId=4611` tras resolver el bloqueo previo en `WF-02`.

## Contexto

- Fase MVP relacionada: Fase 7 Eventos.
- Workflow relacionado: `WF-03-Detect-Events` en Spring Boot.
- Tarea Documento 31: `19.35 Reintento reducido WF-03 para Gemini sin texto en deteccion de eventos`.
- Incidencia: `newsId=4611` quedo clasificada como `INCLUSION/Educacion LGTBI+`, pero fallo en `WF03_EVENT_MATCHING` con `Gemini response does not contain candidates[0].content.parts[0].text`.

## Diagnostico

- `newsId=4611` esta en estado `CLASSIFIED`.
- La clasificacion persistida es `INCLUSION`, subcategoria `Educacion LGTBI+`, relevancia `20`, impacto `LOW`, urgencia `LOW`.
- `WF-03` uso Gemini con `models/gemma-4-26b-a4b-it` y registro un fallo sin texto.
- La noticia no tenia evento asociado y existian 21 eventos abiertos de categoria `INCLUSION`, incluyendo candidatos cercanos sobre recursos LGTBI para adolescentes.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/event/application/DetectEventUseCaseTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- `DetectEventUseCase` reintenta una vez si Gemini devuelve respuesta sin texto.
- El reintento mantiene titulo, resumen y eventos candidatos, pero sustituye el contenido completo por un texto operativo reducido.
- El reintento se hace dentro de la misma ejecucion coordinada de `WF03_EVENT_MATCHING`, evitando esperar el `cooldown` entre intento normal y recuperacion.
- Si el reintento tambien falla, se conserva el comportamiento anterior: metrica de fallo y excepcion.

## Pruebas y verificaciones

- Consulta PostgreSQL local de `news_articles`, `news_classifications`, `ai_operation_metrics`, `events` y `event_news` para `newsId=4611`.
- Backend focal: `mvnw.cmd "-Dtest=DetectEventUseCaseTest,ClassifyNewsUseCaseTest" test` OK, 14 tests.

## Nota operativa

Tras desplegar y reiniciar el backend con esta version, la siguiente ejecucion de `WF-03` deberia reintentar `newsId=4611` con contexto reducido y permitir decidir si se agrupa con un evento `INCLUSION` existente o si crea un evento nuevo.
