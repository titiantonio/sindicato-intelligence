---
name: sindicato-frontend-angular-backoffice
description: Usar para implementar o revisar frontend Angular, backoffice, UX/UI, features, core, shared, consumo de API /api/v1 y flujos de revision humana. Activa esta skill ante cualquier tarea Angular, componentes, pantallas o experiencia de usuario del backoffice.
---

# Sindicato Frontend Angular Backoffice

## Proposito

Guia el trabajo frontend de la Fase 11 manteniendo coherencia con el backend y los contratos REST.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 24 – Diseño UX-UI del Backoffice.md`.
- `docs/Documentacion Proyecto/Documento 07 - Arquitectura Frontend (Angular).md`.
- `docs/Documentacion Proyecto/Documento 12 - Diseño API REST.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Reglas frontend

- Respetar estructura `features`, `shared`, `core`.
- Archivos Angular en `kebab-case`.
- Componentes en `PascalCase`.
- Consumir API versionada `/api/v1`.
- No duplicar reglas de negocio en Angular.

## Flujos clave

- Consultar noticias y eventos.
- Revisar analisis y contenido generado.
- Aprobar o rechazar contenido.
- Publicar contenido aprobado.

## Checklist

- Mantener responsive desktop y movil.
- Respetar roles `ADMIN` y `EDITOR`.
- No crear contratos API inventados si no estan documentados o implementados.
