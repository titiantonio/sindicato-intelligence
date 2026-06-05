---
name: sindicato-documentacion-changelog
description: Usar para documentar intervenciones del asistente, crear Markdown en docs/Docs_Asistentes, aplicar español, fecha yyyy_mm_dd, snake_case, versionado Maven y CHANGELOG.md con Keep a Changelog. Activa esta skill ante cualquier cambio documental, versionado o changelog.
---

# Sindicato Documentacion Changelog

## Proposito

Mantiene trazabilidad de intervenciones, versionado y changelog del proyecto.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `AGENTS.md`.
- `CHANGELOG.md`.
- `backend/pom.xml` cuando haya cambios de codigo backend.

## Reglas de documentacion

- Escribir siempre en espanol.
- Documentar cada intervencion en `docs/Docs_Asistentes`.
- Usar Markdown.
- Usar nombres documentales en `snake_case`.
- Si hay fecha, ponerla al inicio con formato `yyyy_mm_dd`.

## Versionado y changelog

- Si cambia codigo del proyecto, incrementar version en el `pom.xml` correspondiente.
- Si cambia codigo del proyecto, registrar entrada en `CHANGELOG.md`.
- Mantener Keep a Changelog 1.1.0.
- No mezclar cambios no relacionados en la misma entrada.

## Plantilla de registro

Usa secciones: Fecha, Objetivo, Contexto, Fase MVP, Archivos modificados, Decisiones, Pruebas o verificaciones.
