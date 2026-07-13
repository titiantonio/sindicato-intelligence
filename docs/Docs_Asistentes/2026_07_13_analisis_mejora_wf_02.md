# Analisis de mejora de WF-02

## Fecha

2026-07-13

## Objetivo

Revisar como mejorar `WF-02-Classify-News` teniendo en cuenta las mejoras recientes aplicadas a `WF-03`, `WF-04` y `WF-05`.

## Contexto

- Fase MVP relacionada: Fase 6, Clasificacion IA.
- Refinamiento operativo relacionado: Fase 12, automatizaciones internas, observabilidad IA y configuracion ADMIN.
- `WF-02` sigue residiendo en Spring Boot.
- `WF-01` permanece como unico workflow n8n activo para captura RSS/XML.

## Archivos revisados

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessPendingClassificationsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/ProcessPendingEventAnalysisUseCase.java`.
- Tests de clasificacion y automatizacion asociados.

## Diagnostico

- `WF-02` ya tiene descarte seguro para `FUERA_DE_AMBITO` e `INFORMACION_INSUFICIENTE`.
- Ya dispone de enriquecimiento controlado desde URL cuando el contexto local es insuficiente.
- Ya existe fallback para Gemini sin texto en noticias fuera de ambito y reintento con contexto reducido para noticias educativas.
- Ya solicita ejecucion inmediata de `WF-03` cuando una noticia queda clasificada y no descartada.
- Las mejoras recientes de `WF-03`, `WF-04` y `WF-05` introducen mas prioridad, trazabilidad, contexto resumido y validaciones defensivas que pueden trasladarse parcialmente a `WF-02`.

## Propuestas priorizadas

1. Priorizar el lote de `WF-02` por urgencia probable antes de llamar a IA.
2. Registrar mas trazabilidad funcional sanitizada del intento normal, enriquecimiento URL, reintento reducido y descarte final.
3. Ajustar el prompt para devolver una `classificationReason` breve en noticias clasificables, sin cambiar todavia el contrato publico si se guarda solo en metricas.
4. Anadir validaciones post-IA de coherencia entre `category`, `relevance`, `impact` y `urgency` para evitar clasificaciones contradictorias.
5. Reprogramar `WF-02` inmediatamente cuando procese lote completo, igual que `WF-03`, para drenar backlog de capturas.
6. Mejorar observabilidad de fallos repetidos por noticia sin persistir datos sensibles ni prompts completos.

## Decisiones

- No se ha implementado codigo en esta intervencion.
- No se ha marcado ninguna tarea del Documento 31 como completada.
- La mejora recomendada debe registrarse como nueva tarea posterior al Sprint 12 si se implementa.

## Verificaciones

- No se ejecutaron pruebas porque la intervencion fue de analisis y documentacion, sin cambios de codigo.
