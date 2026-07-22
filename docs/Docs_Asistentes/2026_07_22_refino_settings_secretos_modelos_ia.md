# Fecha

2026-07-22

# Objetivo

Permitir que ADMIN elimine credenciales configurables de IA y Telegram desde `/settings`, y reducir clics en la seleccion de modelos IA cargandolos al abrir el selector del workflow.

# Contexto

Intervencion sobre Fase 12 / Sprint 12 del MVP, vinculada al centro ADMIN `/settings`, proveedores IA, automatizaciones backend y configuracion de publicacion Telegram.

El comportamiento previo conservaba secretos cuando el campo se enviaba vacio o `null`, pero no tenia una orden explicita para borrarlos. Tambien requeria pulsar el boton manual de carga de modelos antes de seleccionar modelo IA.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/ai/api/UpdateAiProviderSettingRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/api/AiSettingsController.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/application/UpdateAiProviderSettingCommand.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/application/UpdateAiProviderSettingUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/domain/AiProviderSetting.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/api/UpdateTelegramPublicationSettingsRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/api/TelegramPublicationSettingsController.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/UpdateTelegramPublicationSettingsCommand.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/UpdateTelegramPublicationSettingsUseCase.java`
- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/core/models/ai-observability.models.ts`
- `frontend/src/app/core/models/application-settings.models.ts`
- Tests backend y frontend asociados.
- `backend/pom.xml`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se mantiene `null` como “conservar secreto actual”.
- El borrado requiere bandera explicita: `clearApiKey` para IA y `clearBotToken` para Telegram.
- La UI solicita confirmacion antes de eliminar credenciales.
- La carga automatica de modelos se activa al abrir el selector de modelo del workflow y omite el proveedor determinista local.
- El boton de modelos se conserva como `Recargar modelos` para reintentos manuales.
- No se crean migraciones porque las columnas existentes ya admiten ausencia de secreto.

# Pruebas o verificaciones

- Backend focal: `mvn clean "-Dtest=AiSettingsControllerTest,TelegramPublicationSettingsControllerTest,UpdateTelegramPublicationSettingsUseCaseTest" test` OK, 12 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts --include=src/app/core/services/application-settings.service.spec.ts` OK, 22 tests.
- Frontend build: `npm.cmd run build` OK.
