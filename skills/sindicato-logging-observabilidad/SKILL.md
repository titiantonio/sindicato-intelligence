---
name: sindicato-logging-observabilidad
description: Usar siempre que se implementen o revisen logs, observabilidad, trazabilidad operativa, Logback, auditoria tecnica, diagnostico de errores, integraciones IA/n8n/Telegram, casos de uso backend o configuracion de archivos de log. Tambien debe usarse al crear nuevas funcionalidades Spring Boot para asegurar que incluyen logs utiles y seguros.
---

# Sindicato Logging Observabilidad

## Proposito

Asegurar que cada nueva implementacion deje trazabilidad operativa suficiente para diagnosticar ejecuciones reales sin exponer secretos ni mover logica de negocio fuera de Spring Boot.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 06 V2.0- Arquitectura Backend (Spring Boot).md`.
- `AGENTS.md`.

## Reglas de logging

- Usa SLF4J con Logback.
- No uses `System.out.println()` ni `System.err.println()`.
- Niveles permitidos: `INFO`, `WARN`, `ERROR`.
- Escribe logs en application/use cases, adaptadores de infraestructura e integraciones externas cuando aporten trazabilidad.
- Evita logs de dominio puro si obligan a acoplar dominio a frameworks.
- Los controllers no deben contener logica de negocio; si se loguean entradas HTTP, debe ser tecnico y sin datos sensibles.

## Que registrar

- `INFO`: inicio y finalizacion de casos de uso relevantes, ids de entidades, resultados de negocio, conteos de lote y estado final.
- `WARN`: duplicados, descartes, datos insuficientes, reintentos, respuestas inesperadas recuperables y situaciones que requieren seguimiento.
- `ERROR`: excepciones que impiden completar un caso de uso, errores de integracion IA, Telegram, PostgreSQL, n8n o servicios externos.

## Que no registrar

- API keys.
- JWT completos.
- Passwords.
- Refresh tokens.
- Prompts completos extensos.
- Respuestas IA completas extensas.
- Datos personales innecesarios.
- Payloads completos si contienen informacion sensible o mucho volumen.

## Formato recomendado de mensajes

Usa mensajes estables y orientados a busqueda:

```java
log.info("classification started: newsId={}, title='{}'", newsId, title);
log.info("classification completed: newsId={}, classificationId={}, category={}", newsId, classificationId, category);
log.warn("classification skipped because news already has classification: newsId={}", newsId);
log.error("classification failed: newsId={}, reason={}", newsId, exception.getMessage(), exception);
```

## Criterios por capa

- Application: loguea inicio, exito, decisiones relevantes y fallo del caso de uso.
- Infrastructure: loguea llamadas externas, codigos de error, timeouts y fragmentos acotados de respuestas inesperadas.
- API: loguea solo eventos tecnicos necesarios; no sustituyas logs del caso de uso.
- Domain: evita dependencias de logging salvo que exista una justificacion clara y sin acoplamiento a frameworks.

## Configuracion de archivos

La configuracion oficial del backend usa `logback-spring.xml`:

- Consola activa.
- Consola con color por nivel mediante el conversor ANSI de Spring Boot.
- Archivo activo: `logs/sindicato-intelligence.log`.
- Archivo de errores: `logs/errors/sindicato-intelligence-error.log`.
- Rotacion diaria y por tamaño.
- Archivo comprimido en carpetas mensuales.
- Retencion de 90 dias.
- `LOG_PATH` configurable por variable de entorno.

## Checklist antes de terminar

- Verifica que cada caso de uso nuevo tenga logs utiles de inicio y finalizacion.
- Verifica que los errores externos queden en `ERROR` con causa suficiente.
- Verifica que no se loguean secretos.
- Ejecuta tests aplicables.
- Documenta la intervencion en `docs/Docs_Asistentes`.
