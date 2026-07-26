# Documento 12 - Diseño API REST

**Actualizado:** 25/07/2026
**Base URL:** `/api/v1`
**Formato principal:** JSON
**Contrato ejecutable:** Swagger UI y `/v3/api-docs`

## 1. Principios

- API REST versionada.
- JSON en propiedades `camelCase`.
- DTOs de request y response; nunca se exponen entidades JPA.
- JWT Bearer obligatorio salvo endpoints públicos expresos.
- Validación de entrada mediante Jakarta Validation.
- Errores normalizados por el manejador global.
- Autorización efectiva en Spring Security.
- `multipart/form-data` solo para mensajes manuales de Telegram con adjuntos.

## 2. Documentación OpenAPI

En entorno local:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

En producción, ambos recursos están deshabilitados por defecto mediante:

```text
OPENAPI_API_DOCS_ENABLED=false
OPENAPI_SWAGGER_UI_ENABLED=false
OPENAPI_PUBLIC_ACCESS=false
```

El OpenAPI generado desde los controladores es la referencia exacta de parámetros, DTOs y códigos HTTP.

## 3. Autenticación

| Método | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/auth/login` | inicio de sesión |
| `POST` | `/api/v1/auth/refresh` | renovación de tokens |
| `POST` | `/api/v1/auth/forgot-password` | solicitud de recuperación |
| `POST` | `/api/v1/auth/reset-password` | restablecimiento con token |
| `POST` | `/api/v1/auth/request-temporary-password` | solicitud de contraseña temporal |
| `POST` | `/api/v1/auth/change-password` | cambio autenticado |

Ejemplo de login:

```json
{
  "email": "admin@sindicato.es",
  "password": "********"
}
```

Respuesta resumida:

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "user": {
    "id": 1,
    "name": "Admin Sindicato",
    "role": "ADMIN",
    "mustChangePassword": false
  }
}
```

## 4. Dashboard

| Método | Ruta |
| --- | --- |
| `GET` | `/api/v1/dashboard` |

Devuelve métricas operativas y eventos prioritarios para el backoffice.

## 5. Fuentes

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/sources` | listar |
| `POST` | `/api/v1/sources` | crear |
| `PUT` | `/api/v1/sources/{id}` | actualizar |

Acceso `ADMIN`.

## 6. Noticias

| Método | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/news` | alta individual |
| `POST` | `/api/v1/news/bulk` | captura masiva desde n8n |
| `GET` | `/api/v1/news` | listado compatible |
| `GET` | `/api/v1/news/page` | listado paginado del backoffice |
| `GET` | `/api/v1/news/{id}` | detalle |
| `POST` | `/api/v1/news/{id}/discard` | archivar |
| `POST` | `/api/v1/news/{id}/restore` | restaurar |

`/news/bulk` exige rol `ADMIN` y es el punto de entrada de `WF-01`.

## 7. Clasificación y análisis

| Método | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/classifications/classify` | clasificar una noticia |
| `POST` | `/api/v1/analysis/generate` | generar análisis |

Las ejecuciones por lotes se exponen a través del módulo de automatización.

## 8. Eventos

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/events` | listar |
| `GET` | `/api/v1/events/{id}` | detalle consolidado |
| `POST` | `/api/v1/events/detect` | detección puntual |
| `POST` | `/api/v1/events/merge` | fusión |
| `POST` | `/api/v1/events/{id}/discard` | archivar |
| `POST` | `/api/v1/events/{id}/restore` | restaurar |

## 9. Contenido

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/content` | listar |
| `GET` | `/api/v1/content/{id}` | obtener |
| `GET` | `/api/v1/content/{id}/detail` | detalle editorial |
| `POST` | `/api/v1/content/generate` | generar desde evento |
| `PUT` | `/api/v1/content/{id}` | editar |
| `POST` | `/api/v1/content/{id}/approve` | aprobar |
| `POST` | `/api/v1/content/{id}/reject` | rechazar |

Toda publicación posterior valida que el contenido esté aprobado.

## 10. Publicaciones

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/publications` | histórico |
| `GET` | `/api/v1/publications/{id}` | obtener |
| `GET` | `/api/v1/publications/{id}/detail` | detalle completo |
| `GET` | `/api/v1/publications/telegram-destinations` | destinos disponibles |
| `POST` | `/api/v1/publications/{id}/publish` | publicar contenido aprobado |
| `POST` | `/api/v1/publications/{id}/schedule` | programar |
| `POST` | `/api/v1/publications/manual` | mensaje manual con adjuntos |

La ruta manual consume `multipart/form-data`.

## 11. Usuarios

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/users` | listar |
| `GET` | `/api/v1/users/{id}` | detalle |
| `POST` | `/api/v1/users` | alta sin password en claro |
| `PUT` | `/api/v1/users/{id}` | actualizar |
| `POST` | `/api/v1/users/{id}/activate` | activar |
| `POST` | `/api/v1/users/{id}/disable` | desactivar |
| `POST` | `/api/v1/users/{id}/lock` | bloquear |
| `POST` | `/api/v1/users/{id}/unlock` | desbloquear |
| `POST` | `/api/v1/users/{id}/reset-temporary-password` | nueva contraseña temporal |
| `DELETE` | `/api/v1/users/{id}` | eliminar si el dominio lo permite |

Acceso `ADMIN`.

## 12. Auditoría

| Método | Ruta |
| --- | --- |
| `GET` | `/api/v1/audit/users` |
| `GET` | `/api/v1/audit/editorial` |

Aceptan límites y fecha de consulta. Acceso `ADMIN`.

## 13. Automatizaciones

| Método | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/automation/classifications/run` | ejecutar `WF-02` |
| `POST` | `/api/v1/automation/events/run` | ejecutar `WF-03` |
| `POST` | `/api/v1/automation/analysis/run` | ejecutar `WF-04` |
| `GET` | `/api/v1/automation/overview` | vista consolidada |
| `GET` | `/api/v1/automation/operations` | operaciones recientes |
| `GET` | `/api/v1/automation/settings` | listar configuración |
| `GET` | `/api/v1/automation/settings/{workflowCode}` | detalle |
| `PUT` | `/api/v1/automation/settings/{workflowCode}` | actualizar |
| `POST` | `/api/v1/automation/settings/{workflowCode}/run` | ejecución manual |

La lectura y escritura de configuración exige `ADMIN`; las ejecuciones manuales están disponibles para `ADMIN` y `EDITOR`.

## 14. IA y configuración

| Método | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/ai/prompts` | versiones técnicas de prompts |
| `GET` | `/api/v1/ai/metrics` | métricas por día |
| `GET` | `/api/v1/ai/providers` | proveedores configurados |
| `PUT` | `/api/v1/ai/providers/{providerCode}` | actualizar proveedor |
| `POST` | `/api/v1/ai/providers/{providerCode}/models` | consultar modelos |
| `GET` | `/api/v1/ai/workflow-settings` | asignaciones por workflow |
| `PUT` | `/api/v1/ai/workflow-settings/{workflowCode}` | actualizar asignación |
| `GET` | `/api/v1/settings/telegram` | configuración Telegram |
| `PUT` | `/api/v1/settings/telegram` | actualizar Telegram |

Acceso `ADMIN`.

## 15. Salud

| Método | Ruta | Seguridad |
| --- | --- | --- |
| `GET` | `/api/v1/health` | pública |

Respuesta esperada:

```json
{
  "status": "UP"
}
```

## 16. Errores y seguridad

- `400`: validación o petición inválida.
- `401`: autenticación ausente o no válida.
- `403`: rol insuficiente o cambio de contraseña obligatorio.
- `404`: recurso inexistente.
- `409`: conflicto de dominio, duplicado o estado incompatible.
- `429`: límite de intentos de autenticación.
- `500`: error técnico sin exponer stack trace ni secretos.

Los mensajes deben ser comprensibles para el cliente y seguros para logs y respuestas.
