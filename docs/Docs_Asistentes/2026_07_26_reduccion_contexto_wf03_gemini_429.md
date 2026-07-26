# 2026-07-26 - Reduccion de contexto WF-03 por Gemini 429

## Objetivo

Comprobar si los fallos repetidos de `WF03_EVENT_MATCHING` en noticias como `507`, `511` y `888` podian estar relacionados con contenido largo, y reducir el contexto enviado a Gemini sin cambiar reglas de dominio.

## Contexto

- Fase Documento 30: Fase 12, automatizaciones internas y observabilidad IA.
- Documento 31: tarea `19.57 Reduccion de contexto WF-03 para Gemini 429`.
- Skill aplicada: `sindicato-ia-n8n-workflows` y `sindicato-spring-backend-ddd`.

## Diagnostico

Datos observados en PostgreSQL:

- `news_id=507`: resumen/contenido de 7348 caracteres.
- `news_id=511`: resumen/contenido de 9026 caracteres.
- `news_id=888`: contenido de 11886 caracteres.

Los fallos registrados son `Gemini event matching request failed with HTTP 429`, que apunta a cuota/rate limit del proveedor. El contenido largo no es la causa semantica directa del 429, pero si aumenta tokens por llamada y puede agotar antes los limites de tokens por minuto o cuota disponible.

## Archivos Modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchPromptBuilder.java`
- `backend/src/test/java/es/sindicato/intelligence/event/application/EventMatchPromptBuilderTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se mantiene el prompt oficial WF-03 y su contrato JSON.
- Se reduce el contexto operativo: titulo, resumen, contenido, descripcion de candidatos y titulos recientes se recortan.
- Si `content` coincide con `summary`, se omite para no duplicar tokens.
- No se cambia dominio, matching, estados ni proveedor IA.

## Verificacion

Backend focal WF-03: `mvnw.cmd "-Dtest=EventMatchPromptBuilderTest,DetectEventUseCaseTest,ProcessPendingEventDetectionUseCaseTest,RunAutomationWorkflowUseCaseTest" test` OK, 22 tests, 0 fallos, 0 errores.
