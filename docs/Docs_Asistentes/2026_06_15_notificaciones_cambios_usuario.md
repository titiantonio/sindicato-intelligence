# Fecha

2026-06-15

# Objetivo

Completar las notificaciones por email ante cualquier cambio administrativo relevante de usuario.

# Contexto

El sistema ya notificaba bloqueo, desactivacion, cambio de password y password temporal. El usuario solicito que tambien se notificara eliminacion, reactivacion, desbloqueo y cualquier cambio de datos de usuario.

# Fase MVP

Fase 11: backoffice Angular y gestion ADMIN de usuarios, con soporte backend en el modulo `user`.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/user/application/UserAccountNotificationSender.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/ChangeUserStatusUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/UpdateUserUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/DeleteUserUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/SmtpNewUserCredentialsEmailSender.java`
- `backend/src/test/java/es/sindicato/intelligence/user/application/*User*UseCaseTest.java`
- `frontend/src/app/features/users/users-page.component.ts`
- `frontend/src/app/features/users/users-page.component.spec.ts`
- `CHANGELOG.md`
- `backend/pom.xml`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Activar desde `INACTIVE` envia email de cuenta activada.
- Activar desde `LOCKED` se trata como desbloqueo y envia email de cuenta desbloqueada.
- Actualizar nombre o rol envia email de datos actualizados.
- Eliminar usuario envia email antes de ejecutar el borrado fisico, siguiendo el patron transaccional ya usado por bloqueo/desactivacion.

# Pruebas o verificaciones

- `mvn "-Dtest=UserControllerTest,*User*UseCaseTest" test`: OK, 20 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/users/users-page.component.spec.ts`: OK, 10 tests.
- `npm.cmd run build`: OK, con warning no bloqueante de presupuesto CSS en `users-page.component.scss`.
