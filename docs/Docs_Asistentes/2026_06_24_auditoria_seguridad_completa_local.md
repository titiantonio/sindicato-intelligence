# Revision de Seguridad Completa Local

Fecha: 2026-06-24

Objetivo: auditar la seguridad local del repositorio Sindicato Intelligence con evidencias de documentacion, codigo, configuracion, migraciones, workflow n8n y verificaciones no destructivas.

Fase MVP relacionada: Sprint 10 Seguridad completado, Sprint 11 Frontend completado y Sprint 12 automatizaciones/observabilidad completado. La auditoria se alinea con los pendientes no bloqueantes del Documento 31: CI/CD, secretos productivos, despliegue Proxmox/Nginx, E2E versionado y normalizacion documental.

## Resumen ejecutivo

El sistema tiene una base de seguridad backend razonable para el MVP: Spring Security stateless, JWT con access token de 15 minutos, refresh de 7 dias, BCrypt, roles `ADMIN`/`EDITOR`, endpoints ADMIN protegidos, cambio obligatorio de password aplicado tambien en backend, auditoria de login/publicaciones/acciones de usuario, validacion estructural de respuestas IA y publicacion Telegram solo sobre contenido aprobado.

Estado final tras remediacion local:

- Corregidos los hallazgos implementables en repositorio: secretos versionados, JWT productivo, rate limiting auth, vulnerabilidades altas frontend, refresh tokens revocables, reset tokens hasheados, token Telegram cifrado en reposo, errores IA saneados, cabeceras Nginx, Docker backend no root y compose local sin secretos hardcodeados.
- Permanece como riesgo residual documentado el uso de `localStorage` para tokens frontend. Se mitiga parcialmente con CSP y ausencia de `innerHTML`/`DomSanitizer.bypassSecurityTrust*`; migrarlo a cookie `HttpOnly` implica redisenar el contrato de autenticacion y CSRF.
- Permanece como contexto externo requerido la auditoria real de Proxmox/Nginx/TLS/firewall/backups y la revocacion operativa de la clave Gemini expuesta fuera del repositorio.

Nota posterior de remediacion 2026-06-24: los hallazgos prioritarios C-01, A-01, A-02, A-03, A-04, M-01, M-03, M-04, M-05 y M-06 fueron corregidos en la intervencion `2026_06_24_correccion_hallazgos_seguridad.md`. Este informe conserva la evidencia, pero los secretos se muestran redactados.

## Verificaciones realizadas

- Documentacion revisada: `docs/00-agent-context.md`, Documento 12, Documento 13, Documento 21, Documento 23, Documento 30 y Documento 31.
- Codigo revisado: `SecurityConfig`, JWT, auth, usuarios, Telegram, IA, guards/interceptor Angular, migraciones Flyway, Docker local y WF-01.
- `.\n8n\validate-workflows.ps1`: OK. WF-01 contiene login tecnico, endpoints esperados y cabecera Bearer.
- Backend focal: `./mvnw.cmd "-Dtest=JwtTokenServiceTest,DatabaseUserDetailsServiceTest,SecurityConfigTest,LoginUseCaseTest,AuthControllerTest,TelegramPublicationSettingsControllerTest,PublicationControllerTest" test`: OK, 23 tests.
- `npm.cmd audit --audit-level=high`: OK tras actualizacion frontend; quedan vulnerabilidades bajas transitivas sin fix no rompedor.
- Busqueda de secretos final: sin clave IA real ni passwords Docker anteriores en codigo/configuracion activa.

## Hallazgos criticos

### C-01 Clave Gemini hardcodeada en archivo versionado

Impacto: una API key real expuesta en git permite consumo no autorizado, costes, filtrado de uso y posible abuso del proveedor IA.

Evidencia:

- `set_ai_env.ps1` contenia `GEMINI_API_KEY` con una clave real de Gemini redactada en este informe.
- `git ls-files` confirma que `set_ai_env.ps1` esta versionado.

Recomendacion:

- Revocar la clave en el proveedor IA.
- Sustituir `set_ai_env.ps1` por una plantilla sin secretos, por ejemplo `set_ai_env.example.ps1`.
- Eliminar el secreto del historial si el repositorio ha sido compartido o remoto.
- Anadir patron de exclusion para scripts/env locales con secretos.

Criterio de aceptacion:

- No hay claves reales en `rg -n "GEMINI_API_KEY|AQ\\."`.
- La nueva clave se carga solo desde variable de entorno o gestor de secretos.
- La clave expuesta aparece revocada.

## Hallazgos altos

### A-01 Configuracion productiva puede heredar secreto JWT por defecto

Impacto: si produccion arranca sin `JWT_SECRET`, podria usar `change-this-jwt-secret-in-production-min-32-bytes`, permitiendo falsificacion de tokens si el valor es conocido.

Evidencia:

- `backend/src/main/resources/application.yml` define `app.security.jwt.secret: ${JWT_SECRET:change-this-jwt-secret-in-production-min-32-bytes}`.
- `backend/src/main/resources/application-prod.yml` no redefine `app.security.jwt.secret` ni fuerza fallo si falta `JWT_SECRET`.
- `JwtConfig` solo valida longitud minima, no rechaza el valor por defecto.

Recomendacion:

- En perfil `prod`, exigir `JWT_SECRET` sin valor por defecto.
- Hacer que `JwtConfig` rechace explicitamente el valor placeholder.
- Cubrirlo con test de arranque/configuracion.

Criterio de aceptacion:

- Con perfil `prod` y sin `JWT_SECRET`, la aplicacion falla al arrancar.
- Con `JWT_SECRET` fuerte, arranca correctamente.

### A-02 Sin rate limiting ni bloqueo por intentos en endpoints publicos de auth

Impacto: `login`, `refresh`, `forgot-password`, `reset-password` y `request-temporary-password` quedan expuestos a fuerza bruta, enumeracion operativa por volumen, abuso de correo y consumo de recursos.

Evidencia:

- `SecurityConfig` marca como publicos `/api/v1/auth/login`, `/refresh`, `/forgot-password`, `/reset-password`, `/request-temporary-password`.
- No se encontro configuracion o componente de rate limiting, throttling, captcha, contador de intentos o bloqueo progresivo.

Recomendacion:

- Implementar control por IP y por email para endpoints publicos de auth.
- Registrar auditoria/metricas de intentos fallidos sin guardar passwords ni tokens.
- Considerar bloqueo temporal de cuenta tras N intentos fallidos, respetando el flujo ADMIN.

Criterio de aceptacion:

- Tests MockMvc prueban que un exceso de intentos devuelve `429`.
- El control distingue login, reset y solicitud temporal.

### A-03 Dependencias frontend con vulnerabilidades altas

Impacto: las vulnerabilidades reportadas incluyen DoS, posibles fugas de cache, lectura arbitraria de archivos en herramientas de build/dev y problemas en dependencias transitivas.

Evidencia:

- `npm.cmd audit --audit-level=high` reporta 17 vulnerabilidades, 13 altas.
- Paquetes afectados: `@angular/common`, `@angular/core`, `@angular/platform-browser`, `@angular/router`, `@babel/core`, `esbuild`, `piscina`, `undici`, `vite`, `ws`.

Recomendacion:

- Ejecutar actualizacion controlada con `npm audit fix` o actualizacion explicita de Angular/build tooling.
- Validar `npm test`, `npm run build` y rutas criticas.

Criterio de aceptacion:

- `npm.cmd audit --audit-level=high` finaliza sin vulnerabilidades altas.
- Suite Angular completa y build OK.

### A-04 Scripts temporales versionados con credenciales y tokens de prueba

Impacto: los scripts facilitan abuso en entornos compartidos, revelan passwords admin de pruebas y pueden imprimir tokens de reset. Si los valores coinciden con entornos reales o persistentes, permiten acceso.

Evidencia:

- `tmp_e2e_validation.ps1`, `tmp_e2e_debug.ps1` y `tmp_reset_admin.ps1` estan versionados.
- Contienen password admin de prueba, login admin, extraccion de `token=...` desde MailHog y salida que podia imprimir el token.

Recomendacion:

- Eliminar scripts temporales del repositorio o moverlos a `scripts/` como utilidades saneadas sin credenciales.
- No imprimir tokens de reset ni access tokens.
- Anadir `tmp_*.ps1` a `.gitignore`.

Criterio de aceptacion:

- `git ls-files -- tmp_*.ps1` no devuelve archivos.
- Los scripts mantenidos no contienen passwords ni imprimen tokens.

## Hallazgos medios

### M-01 Refresh tokens stateless sin revocacion persistida

Impacto: un refresh token robado puede renovarse hasta 7 dias, salvo que el usuario quede inactivo/bloqueado o cambie estado. No hay logout server-side ni rotacion con identificador revocable.

Evidencia:

- `RefreshTokenUseCase` decodifica JWT, verifica `tokenType=REFRESH`, usuario existente y estado.
- No existe tabla de refresh tokens, `jti`, version de sesion ni revocacion.

Recomendacion:

- Persistir refresh tokens hasheados o una version de sesion por usuario.
- Rotar refresh token en cada uso e invalidar el anterior.
- Invalidar sesiones en cambio de password, reset, bloqueo y desactivacion.

Criterio de aceptacion:

- Un refresh token antiguo falla tras rotacion o logout.
- Cambio de password invalida refresh tokens previos.

### M-02 Tokens de sesion persistidos en localStorage

Impacto: cualquier XSS en el backoffice permitiria extraer access y refresh token. Angular escapa interpolaciones por defecto, pero el modelo de almacenamiento aumenta el impacto residual.

Evidencia:

- `frontend/src/app/core/services/auth.service.ts` persiste `accessToken`, `refreshToken` y `user` en `localStorage` mediante `StorageService`.
- No se encontro uso de `innerHTML` ni bypass de sanitizacion en `frontend/src/app`, lo que reduce pero no elimina el riesgo.

Recomendacion:

- Evaluar migrar refresh token a cookie `HttpOnly`, `Secure`, `SameSite`.
- Si se mantiene `localStorage`, reducir vida del refresh, reforzar CSP y evitar cualquier renderizado HTML dinamico.

Criterio de aceptacion:

- Decision documentada.
- Si se mantiene `localStorage`, hay CSP activa y tests de no uso de `innerHTML`/`DomSanitizer.bypassSecurityTrust*`.

### M-03 Ausencia de cabeceras de seguridad en Nginx/frontend

Impacto: falta defensa en profundidad frente a XSS, clickjacking, MIME sniffing, filtrado de referrer y uso inseguro en despliegue.

Evidencia:

- `frontend/nginx.conf` solo define `listen`, `root`, `try_files` y proxy `/api/`.
- No se encontraron `Content-Security-Policy`, `X-Frame-Options`/`frame-ancestors`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy` ni HSTS.

Recomendacion:

- Definir cabeceras en Nginx del frontend.
- Activar HTTPS/HSTS en el proxy productivo cuando exista TLS.

Criterio de aceptacion:

- Respuestas frontend incluyen cabeceras de seguridad.
- Prueba con `curl -I` documentada.

### M-04 Configuracion Docker local expone credenciales y reduce aislamiento de n8n

Impacto: en entornos compartidos o si se reutiliza en produccion, expone PostgreSQL/n8n y permite a nodos n8n leer variables de entorno con credenciales tecnicas.

Evidencia:

- `database/docker-compose.yml` contenia password PostgreSQL y password tecnica n8n hardcodeadas, redactadas en este informe.
- `N8N_SECURE_COOKIE: "false"`.
- `N8N_BLOCK_ENV_ACCESS_IN_NODE: "false"`.
- Puertos `5432`, `5678`, `1025`, `8025` publicados en host.

Recomendacion:

- Mantener este compose solo para desarrollo y crear compose/perfil productivo separado.
- Usar `.env.example` sin secretos y `.env` no versionado.
- En produccion, no publicar PostgreSQL/MailHog/n8n fuera de red interna y activar bloqueo de acceso a env en nodos n8n cuando sea viable.

Criterio de aceptacion:

- Compose productivo no contiene secretos hardcodeados.
- N8n productivo no expone env innecesariamente a Code nodes.

### M-05 Errores de proveedor IA pueden persistir o devolver cuerpo externo

Impacto: si Gemini devuelve payloads con datos sensibles, estos pueden acabar en logs, respuesta API o `ai_operation_metrics.error_message` hasta 500 caracteres.

Evidencia:

- `GeminiAIProvider`, `GeminiAnalysisAIProvider` y `GeminiContentAIProvider` construyen excepciones con `exception.getResponseBodyAsString()`.
- `AiOperationMetricsRecorder.recordFailure` persiste `exception.getMessage()`.
- Controllers de IA devuelven `exception.getMessage()` en error.

Recomendacion:

- Sustituir cuerpo externo por diagnostico acotado: status code, codigo interno y descripcion saneada.
- No devolver mensajes crudos de proveedor a API.
- Mantener solo resumen no sensible en metricas.

Criterio de aceptacion:

- Tests prueban que respuestas de error con `apiKey`, prompt o payload externo no se exponen en API/log/metrica.

### M-06 Secretos Telegram y tokens de reset se guardan en claro en base de datos

Impacto: una lectura de base de datos permite usar bot token Telegram o resetear cuentas mientras el token siga vigente.

Evidencia:

- Estado inicial: `telegram_publication_settings.bot_token` era `VARCHAR(255)` sin cifrado a nivel de aplicacion.
- Estado inicial: `password_reset_tokens.token` era `VARCHAR(255)` con unicidad, no hash.
- Estado final: `PasswordResetTokenHasher` persiste nuevos tokens de recuperacion hasheados.
- Estado final: `SecretTextCipher` cifra `telegram_publication_settings.bot_token` con AES-GCM antes de persistirlo y exige `SETTINGS_ENCRYPTION_KEY` en perfil `prod`.

Recomendacion:

- Invalidar tokens de recuperacion antiguos previos al despliegue.
- Re-guardar o rotar el bot token Telegram tras desplegar esta version para que el valor persistido quede cifrado.
- Custodiar `SETTINGS_ENCRYPTION_KEY` fuera del repositorio.

Criterio de aceptacion:

- Nuevos tokens de reset no se persisten en claro.
- El bot token no aparece legible en dumps ordinarios de base de datos tras guardar la configuracion con esta version.

## Hallazgos bajos

### B-01 Password semilla documentada no cumple politica runtime

Impacto: aumenta riesgo en desarrollo si no se rota inmediatamente. Esta deuda ya aparece documentada en el Documento 31.

Evidencia:

- `V2__seed_admin_user.sql` documenta password inicial `Admin@123`, que no cumple politica runtime de 10 caracteres.
- `dev-startup.md` documenta `n8n@sindicato.es / Admin@123`.

Recomendacion:

- Cambiar semillas de desarrollo a passwords compatibles o forzar reset inicial inmediato.

Criterio de aceptacion:

- Todas las passwords semilla cumplen politica o quedan bloqueadas por cambio obligatorio.

### B-02 Imagenes Docker sin pin de digest

Impacto: menor reproducibilidad y mayor blast radius dentro del contenedor ante compromiso.

Evidencia:

- `database/docker-compose.yml` usa `n8nio/n8n:latest` y `mailhog/mailhog:latest`.
- Estado final: `backend/Dockerfile` ejecuta la aplicacion con usuario no root.
- `frontend/Dockerfile` usa `nginx:alpine` sin pin de digest.

Recomendacion:

- Pin de versiones/digest para imagenes productivas.

Criterio de aceptacion:

- Dockerfiles/compose productivos usan imagenes versionadas o digest.

## Scorecard de seguridad

| Area | Puntuacion | Evidencia principal | Riesgo residual |
| --- | ---: | --- | --- |
| Autenticacion/JWT | 9/10 | `SecurityConfig`, `JwtConfig`, `JwtTokenService`, rate limiting y refresh revocable | Migracion futura a cookie HttpOnly si se redisenia auth |
| Autorizacion | 8/10 | Matriz ADMIN/EDITOR en `SecurityConfig`; guards Angular | Requiere mantener tests al crecer endpoints |
| Frontend Angular | 8/10 | Guards/interceptor OK; no `innerHTML`; npm audit high OK; CSP activa | Tokens en localStorage |
| PostgreSQL/Flyway | 8/10 | Constraints, unicidad, timestamps, auditoria, refresh/reset tokens protegidos | Secretos historicos previos deben invalidarse operativamente |
| n8n WF-01 | 8/10 | Validacion OK, login tecnico y Bearer; compose parametrizado | Endurecimiento productivo depende del despliegue real |
| IA | 8/10 | Schema JSON, prompts versionados, metricas, errores saneados | Revocacion externa de clave Gemini expuesta |
| Telegram | 9/10 | Publica solo `APPROVED`, token enmascarado en API y cifrado en reposo | Gestion operativa de clave `SETTINGS_ENCRYPTION_KEY` |
| Docker/Nginx local | 7/10 | Backend no root, compose sin secretos hardcodeados, headers frontend | Imagenes sin digest y puertos dev publicados |
| Infra real Proxmox/Nginx | 0/10 | Sin evidencia local | No verificado - contexto requerido |

## Checklist de controles faltantes

- Revocar clave Gemini expuesta en el proveedor IA y rotar cualquier uso asociado fuera del repositorio.
- Reescribir historial Git si el repositorio fue compartido o publicado con el secreto.
- Revisar almacenamiento de tokens frontend si se aprueba cambio arquitectonico a cookies `HttpOnly` + CSRF.
- Pin de digest/versiones de imagenes Docker para despliegue productivo.
- Documentar o aportar configuracion real de Proxmox/Nginx/TLS/firewall/backups para auditarla.
- Definir procedimiento operativo de custodia y rotacion de `JWT_SECRET` y `SETTINGS_ENCRYPTION_KEY`.

## Backlog priorizado

| Prioridad | Tarea | Area | Archivos sugeridos | Criterio de aceptacion |
| --- | --- | --- | --- | --- |
| Critica | Revocar Gemini key en proveedor | Secretos/IA | Consola proveedor IA | Clave expuesta revocada y sustituida fuera del repo |
| Alta | Limpiar historial Git si el repo fue compartido | Secretos/Git | Herramienta de history rewrite definida por el equipo | El secreto no aparece en clones nuevos ni historial remoto |
| Alta | Auditar Proxmox/Nginx/TLS/firewall/backups reales | Infra | Configs productivas o checklist firmado | Estado deja de ser `No verificado - contexto requerido` |
| Media | Decidir migracion a cookies HttpOnly | Auth/Frontend | Backend auth, frontend interceptor, CSRF | Decision documentada; si se implementa, tokens dejan `localStorage` |
| Media | Crear compose productivo endurecido | Infra | `docker-compose.prod.yml`, docs | Sin secretos hardcodeados, puertos internos, imagenes pinneadas |
| Baja | Actualizar password semilla dev | Auth/Flyway | `V2__seed_admin_user.sql` en siguiente reset permitido o nueva tarea documental | Semilla cumple politica o fuerza cambio inmediato |
| Baja | Pin de imagenes Docker | Docker | `backend/Dockerfile`, `frontend/Dockerfile`, compose | Imagenes versionadas/digest donde aplique |

## Matriz final de controles

| Control | Estado | Riesgo asociado | Recomendacion | Prioridad | Esfuerzo |
| --- | --- | --- | --- | --- | --- |
| JWT access 15 minutos | Implementado | Robo de access token | Mantener tests de expiracion | Baja | Bajo |
| Refresh 7 dias | Implementado | Ventana de sesion larga | Mantener revocacion/rotacion y tests | Media | Alto |
| JWT secret fuerte obligatorio | Implementado | Firma falsificable con placeholder | Custodia operativa fuera del repo | Alta | Bajo |
| BCrypt para passwords | Implementado | Robo de hashes | Mantener coste razonable | Baja | Bajo |
| Politica complejidad password | Implementado | Password debil | Aplicar tambien a semillas | Baja | Bajo |
| Rate limiting auth | Implementado | Fuerza bruta/abuso correo | Revisar limites con trafico real | Alta | Medio |
| Roles ADMIN/EDITOR backend | Implementado | Escalada por frontend | Mantener tests por endpoint | Media | Bajo |
| Guards Angular | Implementado | UX/defensa secundaria | Mantener como apoyo, no control unico | Baja | Bajo |
| Tokens en localStorage | No verificado - decision requerida | Exfiltracion por XSS | Evaluar cookie HttpOnly + CSRF; CSP ya reforzada | Media | Medio |
| Sin `innerHTML` inseguro | Implementado | XSS por renderizado IA | Mantener busqueda en CI | Baja | Bajo |
| Cabeceras Nginx | Implementado | XSS/clickjacking/MIME sniffing | Validar tambien en proxy productivo real | Media | Bajo |
| Secretos fuera de repo | Implementado | Exposicion de credenciales | Revocacion externa Gemini queda operativa | Critica | Medio |
| WF-01 con Bearer | Implementado | Ingestion no autorizada | Mantener validacion workflow | Baja | Bajo |
| n8n sin acceso env en Code nodes | Implementado local configurable | Lectura de secretos desde workflows | En prod usar `N8N_BLOCK_ENV_ACCESS_IN_NODE=true` | Media | Bajo |
| Publicar solo contenido aprobado | Implementado | Publicacion no revisada | Mantener tests de dominio/API | Baja | Bajo |
| Telegram token enmascarado en API | Implementado | Exposicion por UI/API | Mantener cifrado en reposo y rotacion operativa | Media | Medio |
| Reset token de un solo uso | Implementado | Reutilizacion de token | Nuevos tokens hasheados en DB | Media | Medio |
| Validacion JSON IA | Implementado | Persistir salida invalida | Mantener schemas y tests | Baja | Bajo |
| Errores IA saneados | Implementado | Fuga de payload externo | Mantener tests de saneamiento | Media | Bajo |
| Auditoria login/publicaciones/usuarios | Implementado | Falta trazabilidad | Anadir intentos fallidos/rate limit | Media | Medio |
| Dependencias frontend sin altas | Implementado | Vulnerabilidades conocidas | Revisar vulnerabilidades bajas en upgrade no rompedor | Alta | Medio |
| Proxmox/Nginx productivo | No verificado - contexto requerido | TLS/firewall/backups desconocidos | Aportar configs o checklist operativo | Alta | Medio |
| Facebook/X | No aplica al MVP | Canales fuera de alcance | No implementar sin decision futura | Baja | Bajo |

## Notas de alcance y decisiones

- Se aplicaron remediaciones locales y quedaron documentadas en `2026_06_24_correccion_hallazgos_seguridad.md`.
- `CHANGELOG.md` y `backend/pom.xml` fueron actualizados hasta `0.0.64-SNAPSHOT`.
- La auditoria de Proxmox, TLS real, firewall, backups y secretos productivos queda como `No verificado - contexto requerido`: no hay evidencia local suficiente.
- La revocacion de la clave Gemini y la limpieza de historial Git, si hubo remoto o terceros, son acciones externas al repositorio local.
