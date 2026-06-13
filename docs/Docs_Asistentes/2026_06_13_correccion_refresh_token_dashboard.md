# Fecha

2026-06-13

# Objetivo

Corregir el fallo por el que, tras un periodo de inactividad, el backoffice mostraba "No se pudo cargar el dashboard" y dejaba de cargar eventos hasta cerrar sesion y volver a iniciarla.

# Contexto

La incidencia encaja como mantenimiento correctivo de Sprint 10 Seguridad y Sprint 11 Frontend Angular. El backend ya emitia access token de 15 minutos y refresh token de 7 dias, pero no existia endpoint para renovar sesion ni reintento automatico desde Angular cuando una API protegida devolvia `401`.

# Fase MVP

Fase 11 Frontend Angular, con dependencia de Fase 10 Seguridad.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/auth/application/RefreshTokenUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/api/RefreshTokenRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/api/AuthController.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/RefreshTokenUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/api/AuthControllerTest.java`
- `frontend/src/app/core/models/auth.models.ts`
- `frontend/src/app/core/services/auth.service.ts`
- `frontend/src/app/core/services/auth.service.spec.ts`
- `frontend/src/app/core/interceptors/jwt.interceptor.ts`
- `frontend/src/app/core/interceptors/jwt.interceptor.spec.ts`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se completa el flujo de refresh token previsto por la arquitectura JWT del proyecto mediante `POST /api/v1/auth/refresh`.
- El refresh token se valida en backend como JWT de tipo `REFRESH`, no como access token.
- Los refresh tokens dejan de incluir claims `role` y `roles` para que no puedan autorizar llamadas a endpoints protegidos.
- El frontend reintenta solo peticiones que fallen con `401` y realiza logout si la renovacion tambien falla.
- No se cambia la duracion oficial: access token 15 minutos y refresh token 7 dias.

# Pruebas o verificaciones

- Backend: `mvnw.cmd "-Dtest=JwtTokenServiceTest,RefreshTokenUseCaseTest,AuthControllerTest,SecurityConfigTest" test` ejecutado correctamente con 15 tests, 0 fallos, 0 errores.
- Frontend: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/core/services/auth.service.spec.ts --include=src/app/core/interceptors/jwt.interceptor.spec.ts` ejecutado correctamente con 9 specs, 0 fallos.
- Frontend build: `npm.cmd run build` ejecutado correctamente.
