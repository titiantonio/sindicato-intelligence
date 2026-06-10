# Cierre Sprint 10 seguridad

## Fecha

2026-06-10

## Objetivo

Cerrar Sprint 10 de Seguridad con JWT, roles, proteccion de endpoints y login.

## Contexto

Se implementaron secuencialmente T10.1, T10.2, T10.3 y T10.4 del Documento 31, revisando la documentacion de `docs/Documentacion Proyecto` antes de cada tarea.

## Fase MVP

Sprint 10: Seguridad.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/auth/**`
- `backend/src/main/java/es/sindicato/intelligence/user/**`
- `backend/src/main/java/es/sindicato/intelligence/core/config/JwtConfig.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/JwtSecurityProperties.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/AuthConfig.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/java/es/sindicato/intelligence/auth/**`
- `backend/src/test/java/es/sindicato/intelligence/core/config/SecurityConfigTest.java`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_10_t10_1_jwt.md`
- `docs/Docs_Asistentes/2026_06_10_t10_2_roles_admin_editor.md`
- `docs/Docs_Asistentes/2026_06_10_t10_3_proteccion_endpoints.md`
- `docs/Docs_Asistentes/2026_06_10_t10_4_login.md`
- `CHANGELOG.md`
- `backend/pom.xml`

## Decisiones

- JWT HS256 con access token 15 minutos y refresh token 7 dias.
- Carga de usuario desde `users` con roles `ADMIN` y `EDITOR`.
- Matriz de autorizacion aplicada segun acuerdo: `EDITOR` para consulta y flujo editorial/publicacion, `ADMIN` para endpoints tecnicos y administracion.
- Login implementado en `POST /api/v1/auth/login` con respuesta de tokens y datos de usuario.

## Pruebas o verificaciones

- T10.1: `mvn "-Dtest=JwtTokenServiceTest" test`.
- T10.2: `mvn "-Dtest=DatabaseUserDetailsServiceTest" test`.
- T10.3: `mvn "-Dtest=SecurityConfigTest" test`.
- T10.4: `mvn "-Dtest=AuthControllerTest,LoginUseCaseTest,SecurityConfigTest" test`.
- Cierre Sprint 10: intentado `mvn clean test`, bloqueado por entorno local sin PostgreSQL en `localhost:5432` y sin Docker daemon activo.
- Validacion de seguridad completada con `mvn "-Dtest=JwtTokenServiceTest,DatabaseUserDetailsServiceTest,SecurityConfigTest,LoginUseCaseTest,AuthControllerTest" test`: 11 tests, 0 fallos, 0 errores.

## Versionado y changelog

- Version backend actualizada a `0.0.29-SNAPSHOT`.
- `CHANGELOG.md` actualizado con entradas T10.1 a T10.4 y cierre de Sprint 10.
