# 2026-07-09 Color error adjuntos modal

## Fecha

2026-07-09

## Objetivo

Corregir el color del texto de error de adjuntos en el modal de mensaje manual Telegram para que aparezca en rojo.

## Contexto

Mantenimiento correctivo de Fase 11 sobre el modal de publicaciones manuales.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.scss`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- Documento 31

## Decisiones

- Se sustituye el token inexistente `--color-danger` por `--color-danger-text` para el mensaje y `--color-danger-strong` para el borde.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit`: OK.
