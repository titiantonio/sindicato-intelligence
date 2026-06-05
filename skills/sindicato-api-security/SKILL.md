---
name: sindicato-api-security
description: Usar para diseñar o revisar API REST, endpoints /api/v1, DTOs, validaciones, Spring Security, JWT, roles ADMIN y EDITOR, auditoria y proteccion de endpoints. Activa esta skill ante cualquier cambio de controllers, auth, permisos o contratos REST.
---

# Sindicato API Security

## Proposito

Mantiene contratos REST y seguridad coherentes con el MVP.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 13 - Seguridad y Roles.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Reglas API

- Versionar con `/api/v1`.
- JSON unicamente.
- Propiedades JSON en `camelCase`.
- Usar DTOs de request y response.
- No exponer entidades JPA ni entidades de dominio.
- Controllers sin logica de negocio.

## Reglas seguridad

- JWT obligatorio salvo endpoints publicos explicitamente definidos.
- Access token de 15 minutos.
- Refresh token de 7 dias.
- Roles MVP: `ADMIN` y `EDITOR`.
- Auditar login, logout, aprobaciones, publicaciones y cambios de eventos.

## Checklist

- Validaciones en API sin reglas de dominio pesadas.
- Casos de uso controlan acciones de negocio.
- Errores con formato estable y sin filtrar datos sensibles.
