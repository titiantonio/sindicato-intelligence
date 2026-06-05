# Mejora de AGENTS y creacion de skills del proyecto

## Fecha

2026_06_05

## Objetivo

Actualizar `AGENTS.md` para reflejar un rol integral de plataforma y producto, incorporar `docs/00-agent-context.md` como contexto rapido obligatorio, fijar convenciones documentales en espanol y crear skills especificas para el proyecto.

## Contexto

El usuario solicito revisar `AGENTS.md`, mejorar el rol porque el proyecto incluye backend, frontend, base de datos, n8n, IA, seguridad e infraestructura, y crear skills siguiendo el formato de `skills/skill-creator/SKILL.md`.

## Fase MVP

No aplica a una fase funcional concreta del Documento 30. Es una mejora transversal de instrucciones, documentacion operativa y soporte para agentes.

## Archivos modificados

- `AGENTS.md`.
- `docs/Docs_Asistentes/2026_06_05_ajuste_agents_versionado_changelog.md`.

## Archivos creados

- `skills/sindicato-mvp-architect/SKILL.md`.
- `skills/sindicato-spring-backend-ddd/SKILL.md`.
- `skills/sindicato-flyway-modelo-datos/SKILL.md`.
- `skills/sindicato-api-security/SKILL.md`.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`.
- `skills/sindicato-testing-quality/SKILL.md`.
- `skills/sindicato-documentacion-changelog/SKILL.md`.
- `skills/sindicato-frontend-angular-backoffice/SKILL.md`.
- `docs/Docs_Asistentes/2026_06_05_mejora_agents_y_creacion_skills.md`.

## Decisiones tomadas

- Se sustituyo el rol limitado de Spring Backend Architect por Arquitecto Tecnico de Plataforma y Producto.
- Se mantiene Spring Boot, DDD, Clean Architecture y Modular Monolith como especialidad principal porque la logica de negocio debe residir en backend.
- Se anadio la lectura obligatoria de `docs/00-agent-context.md` antes de revisar documentacion tecnica extensa.
- Se mantuvo `docs/Documentacion Proyecto` como ubicacion actual de documentacion tecnica y se indico que `docs/Documentacion` sera la ubicacion principal si el proyecto se reorganiza.
- Se establecio que documentacion, registros del asistente, skills y respuestas deben estar en espanol.
- Se cambio la convencion de documentacion fechada a `yyyy_mm_dd` y `snake_case`.
- Se crearon skills con carpetas y `name` en `kebab-case` para mantener compatibilidad con el formato de skills.
- Se referenciaron todas las skills del proyecto en `AGENTS.md`.

## Versionado y changelog

No se modifico `backend/pom.xml` porque no hubo cambios en codigo ejecutable.

No se modifico `CHANGELOG.md` porque esta intervencion afecta instrucciones, documentacion operativa y skills, no comportamiento del producto.

## Pruebas y verificaciones

- Se verifico la existencia de `docs/00-agent-context.md`.
- Se verifico que `skills/skill-creator/SKILL.md` existe en el proyecto.
- Se verifico que el documento previo del asistente fue renombrado al formato `snake_case` con fecha inicial.
- No se ejecutaron pruebas automatizadas porque no hubo cambios de codigo ejecutable.
