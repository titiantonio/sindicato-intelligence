# 2026-07-09 Redisenio selector emotes editor manual

## Fecha

2026-07-09

## Objetivo

Mejorar el apartado de emotes del editor manual de publicaciones Telegram para hacerlo mas usable, amplio y coherente visualmente.

## Contexto

Mantenimiento correctivo de Fase 11 sobre el modal de publicaciones manuales Telegram, derivado de las tareas 19.20 y 19.21 del Documento 31.

El editor de texto se mantiene, pero el selector de emotes necesitaba mas opciones, no debia ocultar el cuadro de texto y no debia heredar el fondo verde de los botones PrimeNG.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.scss`
- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Mantener el editor WYSIWYG existente sin introducir dependencias nuevas.
- Colocar el panel de emotes bajo el cuadro editable para no tapar ni desplazar la zona de escritura principal.
- Usar botones propios para emotes, sin `pButton`, evitando estilos de accion primaria o fondo verde.
- Mantener scroll interno del panel para soportar mas emotes sin hacer crecer excesivamente el dialogo.
- Subir la version frontend a `0.0.17`.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- `git diff --check` OK, solo avisos LF/CRLF propios del entorno Windows.
