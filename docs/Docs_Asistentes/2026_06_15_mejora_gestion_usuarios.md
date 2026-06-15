# Fecha

2026-06-15

# Objetivo

Mejorar la pantalla ADMIN de gestion de usuarios y anadir borrado fisico conservador de usuarios desde backend.

# Contexto

Intervencion alineada con Sprint 11 / Fase 11 del MVP, sobre la pantalla Angular `/users` y el modulo backend `user`. Se mantiene la decision de conservar `Desactivar` y `Bloquear` porque representan estados distintos: baja administrativa (`INACTIVE`) y bloqueo reversible por incidencia (`LOCKED`).

# Fase MVP

Fase 11: frontend Angular/backoffice, con ajuste backend de soporte en gestion ADMIN de usuarios.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/user/api/UserController.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/DeleteUserUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/JpaUserRepository.java`
- `frontend/src/app/features/users/users-page.component.*`
- `frontend/src/app/core/services/user-admin.service.ts`
- `CHANGELOG.md`
- `backend/pom.xml`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- La paginacion de usuarios se implementa en frontend sobre `GET /api/v1/users`.
- El borrado fisico solo se permite si no hay referencias funcionales en `generated_content` ni `audit_log`.
- Las dependencias tecnicas eliminables se limpian dentro de una transaccion antes de borrar `users`.
- No se modifican migraciones ya existentes.

# Pruebas o verificaciones

- `mvn "-Dtest=UserControllerTest,*User*UseCaseTest" test`: OK, 16 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/users/users-page.component.spec.ts --include=src/app/core/services/user-admin.service.spec.ts`: OK, 15 tests.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: OK, 78 tests.
- `npm.cmd run build`: OK, con warning no bloqueante de presupuesto CSS en `users-page.component.scss`.
- Browser local: `/users` verificada con sesion ADMIN real y datos reales. No se confirmaron acciones destructivas.
