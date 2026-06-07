# Mejora WF-02 lotes y prompt de clasificacion

## Fecha

2026-06-07

## Objetivo

Revisar el workflow antiguo de clasificacion IA usado antes del proyecto, comparar su estrategia de procesamiento y su prompt con el `WF-02-Classify-News` actual, y trasladar las mejoras compatibles con la arquitectura del MVP.

## Contexto

- Fase MVP: Fase 6, clasificacion IA.
- Documento 30: clasificacion de noticias capturadas mediante IA.
- Documento 31: T5.5, prompt WF-02; T5.6, workflow n8n.
- Skill aplicada: `sindicato-ia-n8n-workflows`.

## Comparacion realizada

- El workflow antiguo ejecutaba la clasificacion periodicamente y limitaba el procesamiento a 10 noticias para no saturar el proveedor IA.
- El workflow actual del proyecto filtraba todas las noticias `CAPTURED` devueltas por la API y podia enviar demasiadas llamadas en una sola ejecucion.
- El prompt antiguo tenia mejores criterios operativos de relevancia e impacto laboral docente, pero hacia la clasificacion directamente desde n8n y escribia en PostgreSQL.
- El prompt actual mantenia el contrato del backend, pero era demasiado generico para priorizar noticias por afectacion laboral docente andaluza.

## Archivos modificados

- `n8n/workflows/wf_02_classify_news.json`
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilderTest.java`
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- Mantener la regla arquitectonica: n8n no clasifica directamente con IA ni persiste clasificaciones; n8n solo orquesta y llama a Spring Boot.
- Anadir `Schedule Trigger` cada 5 minutos a `WF-02-Classify-News`, manteniendo tambien `Manual Trigger` para pruebas.
- Limitar `WF-02` a 10 noticias `CAPTURED` por ejecucion, ordenadas por `id` ascendente.
- Permitir que errores HTTP de una clasificacion no corten toda la ejecucion del workflow.
- Enriquecer el prompt oficial WF-02 con criterios de relevancia, impacto y urgencia inspirados en el workflow antiguo, adaptados al contrato actual: `category`, `subcategory`, `relevance`, `impact`, `urgency`, `keywords`, `entities`, `summary`.
- Mantener las categorias oficiales del MVP y usar `OTROS` cuando la noticia sea insuficiente o no clasificable.

## Verificaciones

- Validado JSON de `n8n/workflows/wf_02_classify_news.json` con `ConvertFrom-Json`.
- `mvn "-Dtest=ClassifyNewsPromptBuilderTest,GeminiAIProviderTest" test`: 6 tests, 0 fallos, 0 errores.
- `mvn test`: 100 tests, 0 fallos, 0 errores.

## Nota operativa

Para aplicar el cambio en pruebas reales hay que reiniciar el backend Spring Boot e importar o actualizar el workflow `WF-02-Classify-News` en n8n. Al activarlo en n8n, procesara como maximo 10 noticias capturadas cada 5 minutos.
