# Cierre Sprint 12 observabilidad IA settings

## Fecha

2026-06-18

## Objetivo

Cerrar Sprint 12 como bloque final de optimizacion y observabilidad IA, manteniendo `WF-01` en n8n y las automatizaciones `WF-02` a `WF-06` migradas a Spring Boot.

## Contexto

El Sprint 12 ya habia consolidado las automatizaciones internas en backend hasta `T12.18`. Quedaban pendientes el versionado tecnico de prompts, metricas IA, monitorizacion operativa adaptada a la migracion backend y renombrar la pantalla ADMIN de `/automation-settings` a `/settings`.

## Fase MVP

Documento 30, Fase 12: optimizacion IA y cierre operativo posterior a Angular.

## Archivos modificados

- `backend/src/main/resources/db/migration/V9__ai_observability.sql`.
- `backend/src/main/java/es/sindicato/intelligence/ai/**`.
- `backend/src/main/java/es/sindicato/intelligence/automation/**`.
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCase.java`.
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentUseCase.java`.
- `frontend/src/app/features/settings/**`.
- `frontend/src/app/core/services/ai-observability.service.ts`.
- `frontend/src/app/core/models/ai-observability.models.ts`.
- `frontend/src/app/app.routes.ts`.
- `frontend/src/app/layout/shell/shell.component.ts`.
- `docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `CHANGELOG.md`.
- `backend/pom.xml`.

## Decisiones

- `WF-01-Capture-News` permanece como unico workflow n8n.
- `WF-02` a `WF-06` no se reintroducen en n8n.
- `T12.3` queda reinterpretada como monitorizacion de `WF-01` externo y automatizaciones backend.
- La ruta principal ADMIN es `/settings`, visible como `Configuracion`; `/automation-settings` queda como redireccion temporal.
- Los prompts se versionan y consultan, pero no se editan desde UI en este cierre.
- Las metricas IA no guardan prompts completos ni secretos.

## Pruebas o verificaciones

- Backend focal:

```powershell
mvn "-Dtest=AiObservabilityControllerTest,ListAiMetricsUseCaseTest,JpaAiObservabilityRepositoryTest,AutomationControllerTest,ClassifyNewsUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest" test
```

Resultado: OK, 23 tests.

- Frontend focal:

```powershell
npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/layout/shell/shell.component.spec.ts --include=src/app/core/services/automation.service.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts
```

Resultado: OK, 16 tests.

- Backend completo:

```powershell
mvn test
```

Resultado: OK, 234 tests. Flyway valido 9 migraciones.

- Frontend completo:

```powershell
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

Resultado: OK, 110 tests.

- Frontend build:

```powershell
npm.cmd run build
```

Resultado: OK, con warnings no bloqueantes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.

- n8n:

```powershell
.\n8n\validate-workflows.ps1
```

Resultado: OK para `WF-01`.
