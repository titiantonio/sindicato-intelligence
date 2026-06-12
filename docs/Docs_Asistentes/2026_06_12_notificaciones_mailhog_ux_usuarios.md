# 2026-06-12 - Notificaciones MailHog y UX de usuarios

## Fecha
2026-06-12

## Objetivo
Alinear la gestion de usuarios con el plan de UI/UX consistente y notificaciones SMTP usando MailHog en desarrollo local.

## Contexto
Trabajo asociado a T10.5 y T11.10. No se introduce proveedor externo de email. El canal local sigue siendo MailHog:

- SMTP: `localhost:1025`
- UI: `http://localhost:8025`

`database/docker-compose.yml` mantiene el servicio `mailhog` con puertos `1025:1025` y `8025:8025`.

## Cambios backend
- Creado el puerto `UserAccountNotificationSender` para notificaciones de cuenta.
- Extendida la implementacion SMTP basada en `JavaMailSender` y `SimpleMailMessage` para enviar:
  - password temporal generada o regenerada;
  - confirmacion de cambio de password;
  - aviso de cuenta bloqueada;
  - aviso de cuenta desactivada.
- Integrados los casos de uso `CreateUserUseCase`, `ResetTemporaryPasswordUseCase`, `ChangePasswordUseCase`, `ResetPasswordUseCase` y `ChangeUserStatusUseCase`.
- Los logs no incluyen passwords ni tokens; solo tipo de operacion/destinatario o identificadores operativos.

## Cambios frontend
- `change-password` usa el mismo patron visual de las pantallas de recuperacion/reset, con validacion `PASSWORD_PATTERN` y mensaje de confirmacion antes de cerrar sesion.
- La gestion de usuarios muestra mensajes de exito/error tras acciones administrativas.
- Colores de botones alineados por accion:
  - activar/desbloquear: verde;
  - reset temporal: amarillo;
  - bloquear/desactivar: rojo;
  - editar/limpiar: neutro.

## Pruebas
- Backend: `mvn "-Dtest=ChangePasswordUseCaseTest,ResetPasswordUseCaseTest,ChangeUserStatusUseCaseTest,ResetTemporaryPasswordUseCaseTest,AuthControllerTest,UserControllerTest" test` -> 16 tests, 0 fallos, 0 errores.
- Frontend: `npm.cmd run build` -> correcto.

## Verificacion manual pendiente
- Levantar MailHog con Docker Compose.
- Ejecutar alta/reset temporal/cambio de password/bloqueo/desactivacion.
- Confirmar correos en `http://localhost:8025`.