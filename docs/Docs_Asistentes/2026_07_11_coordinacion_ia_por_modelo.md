# Coordinacion IA por modelo

## Fecha

2026-07-11.

## Objetivo

Evitar que acciones IA manuales y automatizadas se pisen cuando usan el mismo modelo, aplicando una espera y cooldown configurable por ADMIN desde `/settings`.

## Contexto

La peticion afecta a la Fase 12 del MVP: automatizaciones internas Spring Boot, observabilidad IA y configuracion ADMIN. `WF-01` sigue en n8n; `WF-02` a `WF-06` permanecen en Spring Boot.

## Archivos Modificados

- `backend/src/main/resources/db/migration/V21__ai_workflow_model_cooldown.sql`.
- `backend/src/main/java/es/sindicato/intelligence/ai/**`.
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentUseCase.java`.
- `frontend/src/app/features/settings/settings-page.component.*`.
- `frontend/src/app/core/models/ai-observability.models.ts`.
- `frontend/src/app/core/services/ai-observability.service.spec.ts`.
- `CHANGELOG.md`.
- `backend/pom.xml`.
- `frontend/package.json` y `frontend/package-lock.json`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones

- La unidad de coordinacion es `modelName`, no proveedor IA.
- El cooldown por defecto es de 60 segundos.
- El cooldown queda persistido en `ai_workflow_settings.cooldown_seconds` y configurable por workflow desde `/settings`.
- El coordinador es reentrante para que un workflow automatico por lotes bloquee el modelo durante todo el lote sin autobloquear sus llamadas internas.
- Si dos workflows usan modelos distintos, el coordinador no bloquea la ejecucion por modelo.

## Pruebas o Verificaciones

- Backend focal: `mvnw.cmd "-Dtest=AiModelExecutionCoordinatorTest,RunAutomationWorkflowUseCaseTest,ClassifyNewsUseCaseTest,DetectEventUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest,AiSettingsControllerTest,AIProviderSelectionTest,ClassificationControllerTest,AnalysisControllerTest,ContentControllerTest" test` OK, 33 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 17 tests.
