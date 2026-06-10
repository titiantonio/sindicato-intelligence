# T10.2 Roles ADMIN y EDITOR

## Fecha

2026-06-10

## Objetivo

Implementar el soporte de roles `ADMIN` y `EDITOR` y la carga de usuario para autenticacion.

## Contexto

Se revisaron el Documento 31 (T10.2), Documento 13 (roles MVP), Documento 21 (convenciones Java/arquitectura/logging) y el modelo de tabla `users` ya disponible en Flyway.

## Fase MVP

Sprint 10: Seguridad.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserRole.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserAccount.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/UserRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/user/domain/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/UserEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/JpaUserRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/user/infrastructure/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/UserSecurityDetails.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/DatabaseUserDetailsService.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/AuthConfig.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/infrastructure/DatabaseUserDetailsServiceTest.java`
- `CHANGELOG.md`

## Decisiones

- Se modela `UserRole` con valores oficiales `ADMIN` y `EDITOR`.
- Se crea `UserAccount` de dominio y puerto `UserRepository` para desacoplar autenticacion de infraestructura.
- Se implementa adaptador `JpaUserRepository` sobre la tabla `users` existente.
- Se implementa `DatabaseUserDetailsService` con mapeo de roles a authorities Spring `ROLE_ADMIN`/`ROLE_EDITOR`.
- Se incorpora `AuthConfig` con `PasswordEncoder` BCrypt, `DaoAuthenticationProvider` y `AuthenticationManager`.
- Se agregan logs `INFO/WARN` en carga de usuario para trazabilidad de autenticacion sin exponer credenciales.

## Pruebas o verificaciones

- Verificado con `mvn "-Dtest=DatabaseUserDetailsServiceTest" test`: 2 tests, 0 fallos, 0 errores.
