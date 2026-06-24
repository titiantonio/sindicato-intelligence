# Fecha

2026-06-24

# Objetivo

Permitir que un usuario `ADMIN` configure proveedores IA desde la aplicacion, guarde claves API cifradas, cargue modelos disponibles y asigne proveedor/modelo por workflow IA.

# Contexto

Fase MVP afectada: Fase 12, configuracion ADMIN, automatizaciones internas y observabilidad IA.

La configuracion IA por `ADMIN` estaba documentada como pendiente historico y hasta ahora dependia de configuracion tecnica global (`app.ai.provider`). La intervencion traslada la seleccion de proveedor/modelo a PostgreSQL para `WF-02`, `WF-03`, `WF-04` y `WF-05`, manteniendo `WF-06` fuera por no invocar IA.

# Archivos modificados

- Backend: modulo `ai`, proveedores IA de `classification`, `event`, `analysis` y `content`, migracion Flyway `V11__ai_provider_workflow_settings.sql`.
- Frontend: modelos/servicio de observabilidad IA y pantalla `/settings`.
- Tests backend y frontend focales.
- `backend/pom.xml`, `CHANGELOG.md` y Documento 31.

# Decisiones

- Gemini queda como primer proveedor externo funcional; `deterministic` se mantiene para desarrollo y pruebas.
- Las claves API IA se guardan cifradas mediante `SecretTextCipher` y solo se exponen como booleano/enmascarado.
- Las llamadas a Gemini usan cabecera `x-goog-api-key`, evitando enviar claves en query string.
- `WF-03` incorpora proveedor Gemini para matching de eventos.
- La seleccion runtime se resuelve por workflow desde `ai_workflow_settings`, no desde `app.ai.provider`.

# Pruebas o verificaciones

- Backend focal: `mvn "-Dtest=AIProviderSelectionTest,AiSettingsControllerTest,AiObservabilityControllerTest,JpaAiObservabilityRepositoryTest,GeminiAiProviderModelClientTest,GeminiAIProviderTest,GeminiAnalysisAIProviderTest,GeminiContentAIProviderTest" test` OK, 28 tests.
- Backend completo: `mvn test` OK, 257 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 17 tests.
- Frontend build: `npm.cmd run build` OK con warnings preexistentes de presupuesto inicial, `users`, `sources` y `audit`.
