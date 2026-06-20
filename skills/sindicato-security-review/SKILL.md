---
name: sindicato-security-review
description: Usar para realizar auditorias transversales de seguridad del proyecto Sindicato Intelligence en backend Spring Boot, Angular, PostgreSQL/Flyway, n8n, IA, Docker, Nginx, Proxmox, Telegram, JWT, roles, auditoria, secretos y proteccion de datos. Activa esta skill ante peticiones de revision de seguridad, hardening, riesgos, controles, despliegue seguro, secretos, autenticacion, autorizacion, privacidad o auditoria completa del sistema.
---

# Sindicato Security Review

## Proposito

Realiza revisiones de seguridad completas y accionables del proyecto Sindicato Intelligence, respetando el MVP vigente y la arquitectura documentada.

Esta skill complementa a `sindicato-api-security`: usa `sindicato-api-security` para cambios concretos de API/JWT/endpoints y esta skill para auditorias transversales de toda la plataforma.

## Contexto del MVP

- Plataforma interna para inteligencia informativa de un sindicato de docentes de Andalucia.
- Stack oficial: Spring Boot, Angular, PostgreSQL, Flyway, n8n, IA, Docker, Nginx, Proxmox y Telegram.
- Autenticacion: Spring Security con JWT.
- Roles oficiales: `ADMIN` y `EDITOR`.
- Canal de publicacion operativo del MVP: Telegram.
- Facebook y X no forman parte del MVP activo. Solo deben mencionarse como alcance futuro si la documentacion o el usuario lo piden de forma explicita.

## Documentacion a revisar

Antes de emitir una auditoria, revisa la documentacion aplicable:

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 13 - Seguridad y Roles.md`.
- `docs/Documentacion Proyecto/Documento 30 - MVP Tecnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 31 - Plan de Implementacion Detallado.md`.
- `docs/Documentacion Proyecto/Documento 21 - Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseno API REST.md` si la revision afecta endpoints.
- `docs/Documentacion Proyecto/Documento 23 - Catalogo de Prompts IA.md` si la revision afecta IA.

Si los nombres exactos tienen caracteres tipograficos o tildes diferentes, localiza el documento equivalente por numero y titulo.

## Metodologia obligatoria

- Basa la revision solo en evidencias visibles: codigo, configuracion, migraciones, workflows, documentacion y salidas de comandos.
- Si falta evidencia de un control, no lo marques como vulnerabilidad critica por defecto. Usa el estado `No verificado - contexto requerido` e indica que archivo o informacion falta.
- No dupliques hallazgos. Si un problema afecta varias capas, reportalo una vez en la categoria mas representativa o en `Riesgos transversales`.
- Asocia cada hallazgo a rutas concretas cuando existan, por ejemplo `backend/src/main/resources/application.yml`, `SecurityConfig.java`, `docker-compose.yml` o `n8n/workflows/wf_01_capture_news.json`.
- Diferencia entre riesgo real, deuda tecnica, mejora defensiva y alcance futuro.
- Reconoce controles ya aportados por frameworks si estan configurados de forma estandar y hay evidencia: Spring Security, validacion Bean Validation, sanitizacion de plantillas Angular, Flyway, constraints SQL, etc.
- No propongas tecnologias nuevas sin justificar por que encajan con el stack oficial y sin marcarlo como decision pendiente.

## Areas de revision

### Autenticacion

- Flujo de login y refresh token.
- Duracion de access token de 15 minutos y refresh token de 7 dias.
- Almacenamiento de passwords con hash robusto.
- Recuperacion de password, tokens temporales y expiracion.
- Cambio obligatorio de password inicial si aplica.
- Proteccion contra fuerza bruta, bloqueo de cuentas y abuso.
- Gestion de sesion en frontend y renovacion de tokens.

### Autorizacion

- Matriz de permisos para `ADMIN` y `EDITOR`.
- Proteccion de endpoints en backend, no solo en Angular.
- Restricciones de rutas y componentes en frontend como defensa secundaria.
- Riesgos de escalada vertical u horizontal.
- Uso coherente de authorities `ROLE_ADMIN` y `ROLE_EDITOR`.

### Backend y API

- Configuracion Spring Security DSL.
- Mapeo de endpoints publicos y protegidos.
- Validacion de entradas y contratos DTO.
- Errores sin stack traces ni datos sensibles.
- CORS restrictivo.
- CSRF segun modo de autenticacion y almacenamiento de tokens.
- Cabeceras de seguridad.
- Rate limiting o controles equivalentes para endpoints sensibles.
- SQL injection, deserializacion insegura y dependencias vulnerables.

### Frontend Angular

- Guards, interceptores y expiracion de sesion.
- Almacenamiento de tokens y riesgos XSS.
- Renderizado seguro de contenido generado por IA.
- Manejo de errores sin filtrar detalles tecnicos.
- Coherencia visual de permisos con backend.

### Base de datos y Flyway

- Roles de base de datos y minimo privilegio.
- Constraints, indices, claves foraneas y unicidad.
- Migraciones Flyway no modificadas tras ejecucion.
- Proteccion de datos sensibles persistidos.
- Auditoria e integridad temporal con `TIMESTAMP WITH TIME ZONE`.

### n8n

- WF-01 como workflow n8n vigente del MVP.
- Credenciales nativas o variables seguras, sin tokens hardcodeados.
- Autenticacion contra backend y proteccion de webhooks.
- Limites de ejecucion, reintentos y exposicion de endpoints internos.

### IA

- Mitigacion de prompt injection.
- Uso exclusivo de prompts oficiales.
- Validacion estricta de salida JSON antes de persistir.
- Revision humana antes de publicar.
- Evitar envio de secretos, PII innecesaria o datos internos sensibles a proveedores IA.
- Control de costes, limites de uso, timeouts, reintentos y trazabilidad.

### Telegram e integraciones externas

- Gestion segura de bot token, chat id y base URL.
- No exponer token completo en API ni logs.
- Control de errores y rate limits de Telegram.
- Publicar solo contenido aprobado.
- Registrar auditoria de publicaciones exitosas y fallidas.

### Infraestructura

- Docker Compose, redes, volumenes y usuarios no root cuando aplique.
- Nginx, HTTPS, TLS, headers y proxy inverso.
- Proxmox, firewall, SSH y segregacion de servicios.
- Backups de PostgreSQL y volumenes.
- Plan de recuperacion y pruebas de restauracion.

### Riesgos transversales

- Secretos en codigo, `.env`, logs, n8n, Docker o base de datos.
- Auditoria de login, logout, aprobaciones, publicaciones y cambios de eventos.
- Proteccion de datos personales y cumplimiento RGPD.
- Retencion de datos, borrado y minimizacion.
- Observabilidad sin exponer payloads sensibles.

## Formato de salida

Usa esta estructura para auditorias completas:

```markdown
# Revision de Seguridad

## Resumen ejecutivo

## Hallazgos criticos

## Hallazgos altos

## Hallazgos medios

## Hallazgos bajos

## Scorecard de seguridad

| Area | Puntuacion | Evidencia principal | Riesgo residual |
| --- | ---: | --- | --- |

## Checklist de controles faltantes

## Backlog priorizado

| Prioridad | Tarea | Area | Archivos sugeridos | Criterio de aceptacion |
| --- | --- | --- | --- | --- |

## Matriz final de controles

| Control | Estado | Riesgo asociado | Recomendacion | Prioridad | Esfuerzo |
| --- | --- | --- | --- | --- | --- |
```

Estados permitidos en la matriz:

- `Implementado`.
- `Faltante`.
- `No verificado - contexto requerido`.
- `No aplica al MVP`.

Prioridades permitidas:

- `Critica`.
- `Alta`.
- `Media`.
- `Baja`.

Esfuerzos permitidos:

- `Bajo`.
- `Medio`.
- `Alto`.

## Criterios de calidad

- Cada hallazgo debe explicar impacto, evidencia, recomendacion y archivos afectados.
- Las recomendaciones deben ser implementables dentro del stack oficial.
- Las tareas del backlog deben ser pequenas, verificables y ordenadas por riesgo.
- No conviertas controles futuros como MFA, Docker Secrets o nuevos canales sociales en requisitos MVP salvo decision documentada.
- Si la auditoria propone cambios de codigo, indica tambien pruebas esperadas: unitarias, MockMvc, Angular, validacion Flyway, validacion n8n o verificacion Docker segun corresponda.
