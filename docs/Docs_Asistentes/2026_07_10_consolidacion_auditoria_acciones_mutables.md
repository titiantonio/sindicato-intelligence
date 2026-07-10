# Consolidacion de auditoria de acciones mutables

## Fecha

2026-07-10

## Objetivo

Comprobar y reforzar que las acciones mutables disponibles en el backoffice y API quedan registradas en auditoria de usuarios o auditoria editorial segun corresponda.

## Contexto

La intervencion corresponde a una tarea transversal posterior a Sprint 12, dentro de seguridad, auditoria operativa y configuracion ADMIN. Se revisaron `docs/00-agent-context.md`, Documento 13, Documento 31 y las skills `sindicato-api-security`, `sindicato-security-review` y `sindicato-documentacion-changelog`.

## Fase MVP

Posterior a Sprint 12. Consolidacion de seguridad/auditoria sobre funcionalidades ya implementadas.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/audit/application/AuditDetailFormatter.java`
- `backend/src/main/java/es/sindicato/intelligence/source/application/CreateSourceUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/source/application/UpdateSourceUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/ApproveContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/RejectContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/RunAutomationWorkflowUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/automation/application/UpdateAutomationSettingUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/UpdateTelegramPublicationSettingsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/DeleteUserUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserAuditAction.java`
- Tests unitarios focales asociados.
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se registro auditoria editorial en `audit_log` para fuentes, contenido, automatizaciones y configuracion Telegram.
- Se incorporo auditoria para la generacion directa de analisis IA desde detalle de evento, ademas de las metricas IA existentes.
- La configuracion Telegram se audita sin persistir secretos, tokens ni `chatId` en el detalle.
- El borrado fisico de usuarios registra `USER_DELETED` en `user_audit_log` con `user_id=null`, porque el usuario objetivo deja de existir y la FK impide conservar un registro ligado a su id.
- No se modificaron migraciones ejecutadas.

## Pruebas o verificaciones

- `mvn "-Dtest=CreateSourceUseCaseTest,UpdateSourceUseCaseTest,GenerateContentUseCaseTest,ApproveContentUseCaseTest,RejectContentUseCaseTest,RunAutomationWorkflowUseCaseTest,UpdateAutomationSettingUseCaseTest,UpdateTelegramPublicationSettingsUseCaseTest,DeleteUserUseCaseTest" test`
- Resultado inicial: 19 tests, 0 fallos, 0 errores.
- `mvn "-Dtest=CreateSourceUseCaseTest,UpdateSourceUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest,ApproveContentUseCaseTest,RejectContentUseCaseTest,RunAutomationWorkflowUseCaseTest,UpdateAutomationSettingUseCaseTest,UpdateTelegramPublicationSettingsUseCaseTest,DeleteUserUseCaseTest" test`
- Resultado final: 22 tests, 0 fallos, 0 errores.
