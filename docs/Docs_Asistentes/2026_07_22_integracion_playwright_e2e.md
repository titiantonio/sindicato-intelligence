# Integracion Playwright E2E

## Objetivo

Preparar la Fase 0 documental y operativa para integrar Playwright como herramienta E2E del backoffice Angular.

## Contexto

La tarea corresponde al nuevo Sprint 13 del Documento 31: calidad E2E Playwright. Encaja tras la Fase 11 del Documento 30 porque el backoffice Angular ya esta operativo y existe un pendiente de calidad para crear E2E minimo del flujo MVP.

## Archivos modificados

- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `AGENTS.md`.

## Archivos creados

- `skills/sindicato-playwright-e2e/SKILL.md`.
- `docs/guia_playwright.md`.
- `docs/Docs_Asistentes/2026_07_22_integracion_playwright_e2e.md`.

## Decisiones tomadas

- Playwright queda planificado como suite E2E complementaria, no como sustituto de Karma/Jasmine ni JUnit/Mockito.
- El backlog queda organizado en Sprint 13 con tareas T13.1..T13.6.
- La primera fase tecnica sera no intrusiva: configuracion base y smoke tests mockeados.
- Las pruebas contra backend real se posponen a una fase posterior con datos y usuarios controlados.
- Se crea una skill especifica para evitar futuros E2E con secretos, IA real o Telegram real por error.

## Documento 31

Tarea completada:

- `T13.1`: planificar integracion Playwright y reglas operativas.

Tareas pendientes:

- `T13.2`: integrar Playwright base en `frontend/`.
- `T13.3`: crear smoke tests E2E mockeados.
- `T13.4`: crear suite E2E contra backend local.
- `T13.5`: cubrir flujo editorial MVP controlado.
- `T13.6`: preparar ejecucion CI/CD futura.

## Pruebas y verificaciones

No se ejecutan pruebas tecnicas porque esta intervencion no instala dependencias ni modifica codigo ejecutable. La verificacion realizada es documental: el backlog, `AGENTS.md`, la skill y la guia quedan preparados para iniciar `T13.2`.
