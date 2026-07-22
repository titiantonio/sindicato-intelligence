# Refuerzo de accesibilidad del piloto de eventos

## Fecha

2026-07-22

## Objetivo

Cerrar huecos verificables de WCAG 2.2 AA detectados durante la auditoría del piloto `/events`, sin ampliar todavía la dirección visual al resto del frontend.

## Contexto

- Fase del Documento 30: Fase 11, frontend Angular.
- Backlog del Documento 31: Sprint 14, tarea T14.2.9.
- La validación estética del usuario continúa pendiente en T14.2.8.
- La documentación oficial de Angular confirma que los títulos pueden declararse en las rutas y se actualizan al activarlas.

## Archivos modificados

- `frontend/src/index.html`.
- `frontend/src/app/app.routes.ts`.
- `frontend/src/app/features/events/events-page.component.html`.
- `frontend/src/app/features/events/events-page.component.ts`.
- `frontend/e2e/events-pilot.mock.spec.ts`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `docs/accessibility.md`.
- `docs/guia_playwright.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `CHANGELOG.md`.

## Decisiones

- Cambiar el idioma raíz del documento de `en` a `es`.
- Sustituir el título genérico `Frontend` por `Sindicato Intelligence`.
- Definir un título específico para cada ruta pública, editorial y ADMIN mediante la propiedad `title` de Angular Router.
- Recordar el elemento que abre una confirmación y devolverle el foco en el evento real `onHide` del diálogo.
- Mantener el foco dentro del diálogo mientras permanece abierto.
- Evitar que `aria-describedby` del botón de fusión apunte a una región que también contiene el propio botón.
- Aplicar en Playwright los valores de espaciado de texto de WCAG `1.4.12` y comprobar que no recortan controles textuales.

## Pruebas y verificaciones

- Playwright focal del piloto: `4 passed`.
- Suite Playwright mockeada completa: `10 passed`.
- Idioma y título de `/events`: correctos en DOM renderizado.
- Contención del foco y retorno al disparador tras `Escape`: correctos.
- Reflow a 320 CSS px con espaciado WCAG: sin overflow horizontal global.
- Controles textuales visibles: sin recorte por espaciado.
- Suite Angular completa: `158 SUCCESS`.
- Build de producción: correcto, sin advertencias de presupuesto.

## Versionado

- Frontend actualizado de `0.0.37` a `0.0.38`.
- Cambios registrados en `CHANGELOG.md` bajo `Unreleased`.
