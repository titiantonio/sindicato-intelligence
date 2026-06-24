# Correccion de Hallazgos de Seguridad

Fecha: 2026-06-24

## Objetivo

Corregir los hallazgos prioritarios detectados en la auditoria de seguridad local sin modificar arquitectura funcional ni decisiones del MVP.

## Contexto

Intervencion de mantenimiento correctivo posterior a Sprint 12. Las tareas se registraron en el Documento 31 como `T12.28 - Corregir hallazgos prioritarios de la auditoria de seguridad local`, `T12.29 - Implementar refresh tokens revocables y rotables`, `T12.30 - Persistir tokens de recuperacion de password en formato hasheado` y `T12.31 - Cifrar token Telegram configurable en reposo`.

## Archivos modificados

- `.gitignore`
- `CHANGELOG.md`
- `backend/pom.xml`
- `backend/Dockerfile`
- `backend/src/main/resources/application-prod.yml`
- `backend/src/main/java/es/sindicato/intelligence/core/config/JwtConfig.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/AuthRateLimitingFilter.java`
- `backend/src/main/java/es/sindicato/intelligence/core/security/SecretTextCipher.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/GeneratedRefreshToken.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/PasswordResetTokenHasher.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/RequestPasswordResetUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/RefreshTokenHasher.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/RefreshTokenRecord.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/RefreshTokenRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/JwtTokenService.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/LoginUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/RefreshTokenUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/ChangePasswordUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/ResetPasswordUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/RefreshTokenEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/infrastructure/JpaRefreshTokenRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/ChangeUserStatusUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/JpaTelegramPublicationSettingsRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/application/AiErrorSanitizer.java`
- `backend/src/main/java/es/sindicato/intelligence/ai/application/AiOperationMetricsRecorder.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/GeminiContentAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/api/ClassificationController.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/api/AnalysisController.java`
- `backend/src/main/java/es/sindicato/intelligence/content/api/ContentController.java`
- `backend/src/test/java/es/sindicato/intelligence/core/config/JwtConfigTest.java`
- `backend/src/test/java/es/sindicato/intelligence/core/security/SecretTextCipherTest.java`
- `backend/src/test/java/es/sindicato/intelligence/core/config/AuthRateLimitingFilterTest.java`
- `backend/src/test/java/es/sindicato/intelligence/ai/application/AiErrorSanitizerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/LoginUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/RequestPasswordResetUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/RefreshTokenUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/ChangePasswordUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/auth/application/ResetPasswordUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/user/application/ChangeUserStatusUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/infrastructure/JpaTelegramPublicationSettingsRepositoryTest.java`
- `backend/src/main/resources/db/migration/V10__refresh_tokens.sql`
- `backend/src/main/resources/application.yml`
- `database/docker-compose.yml`
- `database/.env.example`
- `frontend/nginx.conf`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_24_auditoria_seguridad_completa_local.md`
- `set_ai_env.example.ps1`

Archivos eliminados:

- `set_ai_env.ps1`
- `tmp_e2e_debug.ps1`
- `tmp_e2e_validation.ps1`
- `tmp_reset_admin.ps1`

## Decisiones tomadas

- Se retiro del repositorio el script local con clave Gemini y se sustituyo por una plantilla sin secretos.
- Se anadieron exclusiones para `.env`, `.env.*`, `tmp_*.ps1` y `set_ai_env.ps1`.
- En perfil `prod`, `JWT_SECRET` queda obligatorio y el placeholder de desarrollo se rechaza en runtime.
- Se incorporo rate limiting en memoria para endpoints publicos de autenticacion como defensa inmediata del MVP.
- Los errores de proveedores IA se sanean antes de exponerse por API o persistirse en metricas.
- `database/docker-compose.yml` exige secretos mediante variables de entorno y documenta placeholders en `database/.env.example`.
- Nginx frontend incorpora CSP, `nosniff`, `Referrer-Policy` y `Permissions-Policy`.
- El contenedor backend pasa a ejecutar la aplicacion con usuario no root.
- Se actualizaron dependencias Angular/build tooling para eliminar vulnerabilidades altas.
- Se anadio persistencia hasheada de refresh tokens, rotacion en cada uso y revocacion en cambio/reset de password y bloqueo/desactivacion.
- Se anadio hashing para nuevos tokens de recuperacion de password antes de persistirlos.
- Se anadio cifrado AES-GCM del token Telegram en reposo con clave externa `SETTINGS_ENCRYPTION_KEY`.

## Verificaciones realizadas

- Backend: `./mvnw.cmd -DskipTests compile` OK con version `0.0.64-SNAPSHOT`.
- Backend focal: reportes Surefire OK para tests de JWT, seguridad, rate limiting, auth, controllers IA y saneamiento de errores.
- Backend focal adicional: reportes Surefire OK para refresh tokens revocables, login, cambio/reset de password y cambio de estado de usuario.
- Backend focal adicional: reportes Surefire OK para hashing de tokens de recuperacion de password.
- Backend focal adicional: `./mvnw.cmd "-Dtest=SecretTextCipherTest,JpaTelegramPublicationSettingsRepositoryTest" test` OK, 4 tests.
- Frontend: `npm.cmd audit --audit-level=high` OK sin vulnerabilidades altas; quedan 4 vulnerabilidades bajas transitivas.
- Frontend: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 120 tests.
- Frontend: `npm.cmd run build` OK con avisos de budgets existentes.
- n8n: `.\n8n\validate-workflows.ps1` OK.
- Docker: `docker compose --env-file .env.example config` OK en `database`.
- Busqueda de secretos focal: sin clave IA real ni passwords Docker anteriores en codigo/configuracion activa.

## Pendientes no resueltos en esta intervencion

- Revocar la clave Gemini expuesta fuera del repositorio desde la consola del proveedor.
- Reescribir historial Git si el repositorio fue compartido o publicado con el secreto.
- Reguardar `SETTINGS_ENCRYPTION_KEY` fuera del repositorio y rotarla mediante procedimiento operativo cuando exista despliegue productivo.
- Auditar Nginx/Proxmox productivo real cuando exista contexto operativo.
- Resolver vulnerabilidades bajas transitivas del frontend cuando haya upgrade no rompedor disponible.
