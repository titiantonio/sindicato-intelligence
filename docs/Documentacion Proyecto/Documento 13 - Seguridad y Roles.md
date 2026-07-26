# Documento 13 - Seguridad y roles

**Actualizado:** 25/07/2026
**Ámbito:** backend, frontend, datos, IA, n8n y Telegram

## 1. Modelo de acceso

La API usa Spring Security como Resource Server JWT y no mantiene sesión HTTP:

- access token: 15 minutos;
- refresh token: 7 días;
- issuer configurable;
- claim `roles` transformado en autoridades `ROLE_*`;
- formularios y HTTP Basic deshabilitados en el backend;
- CSRF deshabilitado porque la autenticación API es Bearer stateless.

El frontend conserva la sesión necesaria para operar, pero el backend vuelve a validar cada petición.

## 2. Endpoints públicos

- `/api/v1/health`;
- `/api/v1/auth/login`;
- `/api/v1/auth/refresh`;
- `/api/v1/auth/forgot-password`;
- `/api/v1/auth/reset-password`;
- `/api/v1/auth/request-temporary-password`.

Swagger y OpenAPI pueden ser públicos en local. En producción quedan deshabilitados por defecto.

## 3. Roles

### ADMIN

Acceso completo, incluyendo:

- fuentes;
- usuarios;
- auditoría;
- configuración de automatizaciones;
- proveedores, métricas y prompts IA;
- configuración Telegram.

### EDITOR

Acceso al flujo editorial:

- noticias y eventos;
- generación y análisis autorizado;
- edición, aprobación y rechazo;
- publicación inmediata o programada;
- ejecución manual de automatizaciones permitidas.

No tiene acceso a `/api/v1/users/**`, `/api/v1/audit/**`, `/api/v1/ai/**`, `/api/v1/settings/**` ni gestión de fuentes.

## 4. Matriz resumida

| Área | ADMIN | EDITOR | Público |
| --- | --- | --- | --- |
| Health | sí | sí | sí |
| Auth público | sí | sí | sí |
| Cambio de contraseña | sí | sí | no |
| Dashboard | sí | sí | no |
| Noticias y eventos | sí | sí | no |
| Contenido y publicaciones | sí | sí | no |
| Ejecución de automatizaciones | sí | sí | no |
| Configuración de automatizaciones | sí | no | no |
| Fuentes | sí | no | no |
| Usuarios | sí | no | no |
| Auditoría | sí | no | no |
| IA y Telegram settings | sí | no | no |

## 5. Contraseñas

- Las altas no reciben ni persisten contraseñas en claro.
- Se genera una contraseña temporal.
- Se notifica por email.
- Caduca a los 7 días por defecto.
- El primer acceso obliga al cambio.
- El filtro `ForcePasswordChangeFilter` bloquea el resto de operaciones mientras el cambio esté pendiente.
- La recuperación usa tokens de un solo uso y caducidad limitada.
- Se mantiene historial para evitar reutilizaciones conforme a las reglas del dominio.

## 6. Protección frente a abuso

`AuthRateLimitingFilter` limita intentos en endpoints de autenticación. Los rechazos deben responder sin revelar si una cuenta existe más allá de lo estrictamente necesario.

Las cuentas pueden pasar a:

- `PENDING_ACTIVATION`;
- `ACTIVE`;
- `INACTIVE`;
- `LOCKED`.

## 7. Auditoría

`user_audit_log` registra acciones de identidad:

- login correcto y fallido;
- logout;
- recuperación y cambio;
- alta, activación, bloqueo y cambios de estado.

`audit_log` registra acciones editoriales y operativas:

- cambios y fusiones de eventos;
- contenido generado, editado, aprobado o rechazado;
- publicación y errores;
- automatizaciones relevantes.

Los registros no deben contener contraseñas, tokens completos, claves, prompts extensos ni payloads sensibles.

## 8. Secretos

Secretos protegidos:

- `JWT_SECRET`;
- `SETTINGS_ENCRYPTION_KEY`;
- credenciales PostgreSQL;
- credenciales n8n;
- `GEMINI_API_KEY`;
- token y destinos Telegram.

Reglas:

- `.env` está ignorado por Git;
- `.env.example` solo contiene valores locales de demostración;
- producción debe inyectar secretos externos;
- los valores sensibles configurables se cifran o enmascaran;
- nunca se registran en logs.

Los secretos de demostración deben sustituirse antes de publicar un despliegue.

## 9. IA

- Los prompts oficiales están versionados.
- Las respuestas se validan contra la estructura esperada.
- La IA no ejecuta decisiones editoriales.
- No se persiste una respuesta inválida.
- Las operaciones registran proveedor, modelo, latencia, estado y error resumido.
- Los textos fuente enviados a proveedores deben limitarse al contexto necesario.

## 10. Telegram

- La publicación solo acepta contenido aprobado.
- La integración está deshabilitada por defecto.
- Los destinos se configuran por `ADMIN`.
- Los adjuntos se validan por tipo y tamaño.
- Los errores se registran sin incluir token ni respuesta sensible.
- Ante un timeout, debe comprobarse el estado externo antes de reintentar para evitar duplicados.

## 11. n8n

- Solo aloja `WF-01`.
- Se autentica contra Spring Boot con un usuario técnico.
- No contiene reglas de negocio.
- Las credenciales no deben quedar incrustadas en el JSON versionado.
- La interfaz web debe protegerse y no exponerse sin TLS en producción.

## 12. Configuración de producción

Antes de desplegar:

- cambiar todos los valores de `.env.example`;
- usar HTTPS mediante Nginx;
- restringir CORS al dominio del backoffice;
- mantener Swagger/OpenAPI deshabilitados;
- no exponer PostgreSQL ni MailHog a Internet;
- aplicar rate limiting en Nginx además del control de autenticación;
- montar volúmenes con permisos mínimos;
- definir copias de seguridad y restauración probada;
- revisar retención de logs y datos personales;
- proteger n8n mediante autenticación fuerte y red privada.

## 13. Riesgos pendientes

Para la entrega local, la seguridad funcional está implementada y probada. Para una puesta en producción siguen pendientes:

- gestor de secretos productivo;
- TLS y cabeceras en Nginx desplegado;
- política de backup verificada;
- monitorización e alertas externas;
- revisión de privacidad y base jurídica de los datos de usuario;
- rotación inicial de todas las credenciales.
