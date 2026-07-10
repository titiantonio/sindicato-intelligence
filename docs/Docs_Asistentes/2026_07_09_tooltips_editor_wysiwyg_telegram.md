# 2026-07-09 Tooltips editor WYSIWYG Telegram

## Fecha

2026-07-09

## Objetivo

Anadir tooltips a las funciones del editor WYSIWYG Telegram para que el usuario sepa que hace cada control.

## Contexto

Mantenimiento correctivo de Fase 11 sobre el dialogo de publicaciones manuales Telegram, derivado de la ampliacion del editor WYSIWYG.

## Fase MVP

- Fase 11: backoffice Angular.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Usar `TooltipModule` de PrimeNG, ya presente en el stack frontend.
- Aplicar tooltips a botones de formato, paneles de entidades Telegram, acciones de bloques y emotes.
- Mantener textos breves, orientados a accion y compatibles con el uso por `ADMIN` y `EDITOR`.
- Subir la version frontend a `0.0.19`.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
