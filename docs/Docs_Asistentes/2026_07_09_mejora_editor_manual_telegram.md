# 2026-07-09 Mejora editor manual Telegram

## Fecha

2026-07-09

## Objetivo

Mejorar la experiencia del editor de mensaje manual Telegram para que el formato y los emotes sean mas intuitivos.

## Contexto

Mantenimiento correctivo de Fase 11 sobre el modal de publicaciones manuales.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.scss`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- Documento 31

## Decisiones

- Se sustituye el textarea con etiquetas visibles por un `contenteditable` WYSIWYG basico.
- Se mantiene el envio como HTML compatible con Telegram.
- Se evita introducir dependencias nuevas.
- Los emotes se organizan en grupos para facilitar seleccion rapida.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit`: OK.
