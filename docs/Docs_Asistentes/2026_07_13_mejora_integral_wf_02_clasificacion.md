# Mejora integral WF-02 clasificacion

## Fecha

2026-07-13

## Objetivo

Implementar las mejoras acordadas para `WF-02-Classify-News`, considerando el impacto en `WF-03`, `WF-04` y `WF-05`.

## Contexto

- Fase MVP relacionada: Fase 6, Clasificacion IA.
- Refinamiento operativo relacionado: Fase 12, automatizaciones internas, observabilidad IA y configuracion ADMIN.
- `WF-02` permanece en Spring Boot.
- No se ha reintroducido logica en n8n.
- `WF-03`, `WF-04` y `WF-05` no requieren cambios directos; reciben clasificaciones mas coherentes y priorizadas.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassificationAIResponse.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/DeterministicAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessPendingClassificationsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/ai/domain/AiOperationMetricRepository.java`.
- `backend/src/main/java/es/sindicato/intelligence/ai/infrastructure/JpaAiOperationMetricRepository.java`.
- `backend/src/main/resources/db/migration/V25__ai_operation_metrics_entity_failure_index.sql`.
- Tests backend de clasificacion, automatizacion, Gemini y metricas IA.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `backend/pom.xml`.
- `CHANGELOG.md`.

## Decisiones

- La cuarentena por fallos repetidos no crea nuevo estado de noticia: la noticia permanece `CAPTURED` para revision manual o reintento posterior.
- La cuarentena no consume cupo de llamadas IA y no bloquea noticias posteriores dentro del margen de lookahead.
- `classificationReason` queda como dato interno opcional de metricas IA, sin migracion de `news_classifications` ni cambio de API publica.
- Los descartes `FUERA_DE_AMBITO` e `INFORMACION_INSUFICIENTE` se normalizan defensivamente a relevancia `0`, impacto `LOW`, urgencia `LOW` y sin campos enriquecidos.
- Las respuestas incoherentes se rechazan antes de persistir y quedan registradas como fallo operativo.
- `WF-02` se reprograma inmediatamente solo cuando procesa un lote completo con trabajo real, evitando bucles si todos los elementos quedan saltados por cuarentena.

## Pruebas o verificaciones

- `mvn -q "-Dtest=ClassifyNewsUseCaseTest,ClassifyNewsPromptBuilderTest,GeminiAIProviderTest,DeterministicAIProviderTest,ProcessPendingClassificationsUseCaseTest,RunAutomationWorkflowUseCaseTest,JpaAiOperationMetricRepositoryTest" test` OK.
- `mvn -q -DskipTests compile` OK.

## Documento 31

- Registrada y marcada como completada la tarea `19.39 Mejora integral WF-02 clasificacion IA - 2026-07-13`.
