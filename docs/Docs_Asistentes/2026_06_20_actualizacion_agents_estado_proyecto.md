# 2026-06-20 - Actualizacion de AGENTS al estado operativo del proyecto

## Objetivo

Revisar el estado actual del proyecto y actualizar `AGENTS.md` para que refleje los cambios acumulados tras seguridad, frontend, automatizaciones, configuracion ADMIN, observabilidad IA y auditoria.

## Contexto

Se revisaron `docs/00-agent-context.md`, `Documento 30`, `Documento 31`, `Documento 09 V2.0`, las skills de documentacion y MVP, la estructura real de backend/frontend/n8n y los registros recientes de `docs/Docs_Asistentes`.

La principal desviacion detectada era que `AGENTS.md` seguia describiendo `WF-02` a `WF-06` como workflows n8n, cuando el estado operativo actual mantiene solo `WF-01` en n8n y ejecuta el resto mediante Spring Boot, scheduler, API y backoffice.

## Fase MVP

Mantenimiento documental posterior a Sprint 12. No se implementa nueva funcionalidad.

## Archivos modificados

- `AGENTS.md`
- `docs/Docs_Asistentes/2026_06_20_actualizacion_agents_estado_proyecto.md`

## Decisiones

- Mantener la secuencia del Documento 30 como referencia historica y de orden, pero anadir el estado operativo actual de Sprint 10, Sprint 11 y Sprint 12.
- Documentar que `WF-02` a `WF-06` no deben recrearse en n8n salvo peticion explicita y decision arquitectonica documentada.
- Anadir modulos actuales: `auth`, `automation`, `ai`, `audit`, `dashboard`, `health` y `core`.
- Anadir tablas actuales: `password_reset_tokens`, `user_audit_log`, `audit_log`, `automation_workflow_settings`, `telegram_publication_settings`, `ai_prompt_versions` y `ai_operation_metrics`.
- Actualizar estados actuales de `Publication` y `User`.
- Actualizar referencias a `/settings`, `/audit`, observabilidad IA, configuracion Telegram, auditoria visible y publicacion programada.
- No actualizar `pom.xml` ni `CHANGELOG.md`, porque el cambio es documental y no modifica codigo de aplicacion.

## Verificaciones

- Revisada la estructura real de carpetas en `backend/src/main/java/es/sindicato/intelligence`.
- Revisadas las features reales de Angular en `frontend/src/app/features`.
- Verificado que `n8n/workflows` conserva solo `wf_01_capture_news.json`.
- Revisado `SecurityConfig.java` para confirmar proteccion actual de endpoints.
- Revisados controllers de contenido, publicaciones, usuarios, automatizaciones, auditoria y observabilidad IA para corregir rutas de referencia en `AGENTS.md`.

## Pruebas

No se ejecutan pruebas backend ni frontend porque solo se modifica documentacion operativa para agentes.
