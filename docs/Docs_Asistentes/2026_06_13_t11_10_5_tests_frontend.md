# T11.10.5 Tests frontend auth/users/guards/services

Fecha: 2026-06-13

## Objetivo

Cerrar T11.10.5 con cobertura Angular focal para los flujos criticos de autenticacion, cambio de password obligatorio, recuperacion/reset de password, gestion ADMIN de usuarios y restricciones de navegacion.

## Cambios realizados

- Anadidos tests de `AuthService`, `UserAdminService` y `StorageService`.
- Anadidos tests de `authGuard`, `passwordChangeGuard` y `roleGuard`.
- Anadidos tests de `LoginPageComponent`, `ForgotPasswordPageComponent`, `ResetPasswordPageComponent`, `ChangePasswordPageComponent` y `UsersPageComponent`.
- Validada la creacion de usuarios sin password en payload frontend.
- Validadas acciones administrativas: activar, desactivar, bloquear, desbloquear y reset temporal.

## Verificacion

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: 45 tests OK.
- `npm.cmd run build`: OK.

## Pendientes recomendados

1. Revisar y alinear autenticacion JWT en n8n WF-02..WF-06.
2. Decidir alcance MVP de `POST /api/v1/events/merge`, scheduling de publicaciones y editor manual de contenido.
3. Exponer consulta ADMIN de auditoria si se requiere para operacion del backoffice.