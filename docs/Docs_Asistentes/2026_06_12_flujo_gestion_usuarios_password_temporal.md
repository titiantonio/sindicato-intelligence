# 2026-06-12 - Flujo de gestion de usuarios con password temporal

## Fecha

2026-06-12

## Objetivo

Modificar el flujo de gestion de usuarios para que solo `ADMIN` cree usuarios, sin solicitar password en frontend ni backend, generando password temporal por email, con estado inicial `PENDING_ACTIVATION`, cambio obligatorio en primer login, expiracion configurable, regeneracion de password temporal expirada, auditoria y gestion administrativa de estados.

## Contexto

Intervencion alineada con Sprint 10 T10.5 y Sprint 11 T11.10 del Documento 31. Se parte de un trabajo previo ya existente de recuperacion de password y administracion de usuarios, por lo que se ampliaron las piezas existentes sin revertir cambios previos del arbol de trabajo.

## Fase MVP

- Documento 30: Fase 10, seguridad y usuarios.
- Documento 30: Fase 11, frontend Angular backoffice.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserAccount.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserStatus.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserAuditAction.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserAuditLogRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/*User*.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/*UseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/api/*`
- `backend/src/main/java/es/sindicato/intelligence/user/api/*`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/*`
- `backend/src/main/resources/db/migration/V5__user_account_status_audit.sql`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-prod.yml`
- `frontend/src/app/core/models/auth.models.ts`
- `frontend/src/app/core/models/user-admin.models.ts`
- `frontend/src/app/core/services/auth.service.ts`
- `frontend/src/app/core/services/user-admin.service.ts`
- `frontend/src/app/core/guards/password-change.guard.ts`
- `frontend/src/app/features/auth/change-password/*`
- `frontend/src/app/features/users/*`
- `frontend/src/app/app.routes.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se introduce `UserStatus` con `PENDING_ACTIVATION`, `ACTIVE`, `INACTIVE` y `LOCKED` para no depender solo de `active`.
- La cuenta pendiente puede autenticarse para cambiar password, pero el filtro `ForcePasswordChangeFilter` bloquea cualquier otra funcionalidad mientras `mustChangePassword` siga activo.
- La password temporal se genera en backend, se almacena hasheada, se envia por email y expira mediante `app.security.temporary-password.expiration-days`.
- Las acciones de usuario se auditan en `user_audit_log`: creacion, activacion, desactivacion, bloqueo, desbloqueo, cambios de rol, resets temporales, cambios de password y login.
- No se elimina fisicamente a usuarios; la desactivacion usa estado `INACTIVE`.

## Pruebas o verificaciones

- Ejecutado `npm run build` en `frontend`: correcto.
- Intentado `mvn -Dtest=AuthControllerTest,UserControllerTest,SecurityConfigTest,LoginUseCaseTest,DatabaseUserDetailsServiceTest,JwtTokenServiceTest test` en `backend`, bloqueado porque `JAVA_HOME` no esta definido y no hay JDK visible en `/usr/lib/jvm` dentro de WSL.
- Realizada comprobacion estatica con `rg` sobre constructores, endpoints y campos modificados.
