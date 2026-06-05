---
name: sindicato-mvp-architect
description: Usar para validar fases del MVP, revisar docs/00-agent-context.md, Documento 30, planes de implementacion, dependencias entre fases y decisiones arquitectonicas del proyecto sindicato-intelligence. Activa esta skill siempre que el usuario pida planificar, priorizar, revisar alcance MVP o evitar saltarse fases.
---

# Sindicato MVP Architect

## Proposito

Ayuda a mantener el desarrollo alineado con el MVP tecnico ejecutable y evita implementar funcionalidades fuera de orden.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`.
- `docs/Documentacion Proyecto/Documento 22 – Backlog MVP y Plan de Implementación.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

Si existe `docs/Documentacion`, usala como ubicacion principal reorganizada.

## Flujo de trabajo

1. Identifica la fase MVP afectada.
2. Comprueba dependencias previas en el Documento 30.
3. Resume el plan de implementacion antes de modificar codigo.
4. Rechaza cambios que adelanten fases sin peticion explicita.
5. Documenta la intervencion en `docs/Docs_Asistentes`.

## Criterios de decision

- `Event` es la entidad central.
- Spring Boot contiene la logica de negocio.
- n8n orquesta, no decide reglas de negocio.
- IA apoya clasificacion, analisis y contenido, pero no sustituye reglas de dominio.
