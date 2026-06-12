# 2026-06-11 - T10.5 y T11.10 recuperacion de password y gestion de usuarios

## Objetivo

Implementar en el MVP:

- Flujo completo de "Olvide mi password".
- Gestion de usuarios por `ADMIN` (alta, listado, edicion y desactivacion).
- Integracion local de correo con MailHog para desarrollo.

## Contexto y fase MVP

- Documento 30: extension funcional sobre Fase 10 (Seguridad) y Fase 11 (Frontend Angular).
- Documento 31: se añadieron T10.5 y T11.10 y se registraron avances/estado.

## Archivos modificados

### Backlog y seguimiento

- docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md

### Frontend Angular

- frontend/src/app/app.routes.ts
- frontend/src/app/core/models/auth.models.ts
- frontend/src/app/core/models/user-admin.models.ts
- frontend/src/app/core/services/auth.service.ts
- frontend/src/app/core/services/user-admin.service.ts
- frontend/src/app/features/auth/login/login-page.component.ts
- frontend/src/app/features/auth/login/login-page.component.html
- frontend/src/app/features/auth/login/login-page.component.scss
- frontend/src/app/features/auth/forgot-password/forgot-password-page.component.ts
- frontend/src/app/features/auth/forgot-password/forgot-password-page.component.html
- frontend/src/app/features/auth/forgot-password/forgot-password-page.component.scss
- frontend/src/app/features/auth/reset-password/reset-password-page.component.ts
- frontend/src/app/features/auth/reset-password/reset-password-page.component.html
- frontend/src/app/features/auth/reset-password/reset-password-page.component.scss
- frontend/src/app/features/users/users-page.component.ts
- frontend/src/app/features/users/users-page.component.html
- frontend/src/app/features/users/users-page.component.scss
- frontend/src/app/layout/shell/shell.component.ts

### Backend Spring Boot

- backend/src/main/java/es/sindicato/intelligence/auth/api/AuthController.java
- backend/src/main/java/es/sindicato/intelligence/auth/api/ForgotPasswordRequest.java
- backend/src/main/java/es/sindicato/intelligence/auth/api/ResetPasswordRequest.java
- backend/src/main/java/es/sindicato/intelligence/auth/api/MessageResponse.java
- backend/src/main/java/es/sindicato/intelligence/auth/application/PasswordResetEmailSender.java
- backend/src/main/java/es/sindicato/intelligence/auth/application/PasswordResetTokenRecord.java
- backend/src/main/java/es/sindicato/intelligence/auth/application/PasswordResetTokenRepository.java
- backend/src/main/java/es/sindicato/intelligence/auth/application/RequestPasswordResetUseCase.java
- backend/src/main/java/es/sindicato/intelligence/auth/application/ResetPasswordUseCase.java
- backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/PasswordResetTokenEntity.java
- backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/JpaPasswordResetTokenRepository.java
- backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/SmtpPasswordResetEmailSender.java
- backend/src/main/java/es/sindicato/intelligence/user/domain/UserAccount.java
- backend/src/main/java/es/sindicato/intelligence/user/domain/UserRepository.java
- backend/src/main/java/es/sindicato/intelligence/user/infrastructure/UserEntity.java
- backend/src/main/java/es/sindicato/intelligence/user/infrastructure/JpaUserRepository.java
- backend/src/main/java/es/sindicato/intelligence/user/application/CreateUserCommand.java
- backend/src/main/java/es/sindicato/intelligence/user/application/UpdateUserCommand.java
- backend/src/main/java/es/sindicato/intelligence/user/application/UserNotFoundException.java
- backend/src/main/java/es/sindicato/intelligence/user/application/CreateUserUseCase.java
- backend/src/main/java/es/sindicato/intelligence/user/application/UpdateUserUseCase.java
- backend/src/main/java/es/sindicato/intelligence/user/application/DisableUserUseCase.java
- backend/src/main/java/es/sindicato/intelligence/user/application/ListUsersUseCase.java
- backend/src/main/java/es/sindicato/intelligence/user/application/GetUserUseCase.java
- backend/src/main/java/es/sindicato/intelligence/user/api/CreateUserRequest.java
- backend/src/main/java/es/sindicato/intelligence/user/api/UpdateUserRequest.java
- backend/src/main/java/es/sindicato/intelligence/user/api/UserResponse.java
- backend/src/main/java/es/sindicato/intelligence/user/api/UserController.java
- backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java
- backend/src/main/resources/application.yml
- backend/src/main/resources/db/migration/V5__create_password_reset_tokens.sql
- backend/src/test/java/es/sindicato/intelligence/auth/api/AuthControllerTest.java
- backend/src/test/java/es/sindicato/intelligence/core/config/SecurityConfigTest.java
- backend/src/test/java/es/sindicato/intelligence/user/api/UserControllerTest.java

### Infraestructura y versionado

- database/docker-compose.yml
- backend/pom.xml
- CHANGELOG.md

## Decisiones tecnicas

- Password reset con token temporal persistido en base de datos, expiracion configurable y marcado de uso unico.
- Respuesta de `forgot-password` no revela si el email existe (mensaje neutro).
- Validacion de password en alta y reset: minimo 10 caracteres con mayuscula, minuscula, numero y simbolo.
- Gestion de usuarios restringida a rol `ADMIN` en `SecurityConfig`.
- Envio de correo en entorno local via MailHog (`localhost:1025` SMTP, `localhost:8025` UI).

## Pruebas y verificaciones

- Frontend: build OK con `node node_modules/@angular/cli/bin/ng.js build`.
- Backend: tests focalizados OK con `mvn -Dtest=AuthControllerTest,UserControllerTest,SecurityConfigTest test`.
  - Resultado: 13 tests, 0 fallos, 0 errores.

## Pendientes

- T11.10.5: añadir tests frontend especificos para formularios y servicios de auth/users.
- Validacion E2E manual del correo de recuperacion levantando `mailhog` en Docker junto con backend y frontend.
