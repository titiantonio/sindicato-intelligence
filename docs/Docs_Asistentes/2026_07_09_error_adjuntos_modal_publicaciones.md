# 2026-07-09 Error adjuntos modal publicaciones

## Fecha

2026-07-09

## Objetivo

Mostrar dentro del modal de mensaje manual Telegram los errores de adjuntos demasiado grandes, resaltando el campo afectado.

## Contexto

Mantenimiento correctivo de Fase 11 sobre el flujo de publicaciones manuales Telegram incorporado en la correccion 19.17.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.scss`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- Documento 31

## Decisiones

- Los errores de envio manual se separan del mensaje global de la pagina.
- Los errores relacionados con adjuntos se muestran junto al input de archivo y activan estado visual de error.
- Al cambiar los adjuntos se limpia el error del campo.

## Pruebas o verificaciones

- `npx.cmd tsc -p tsconfig.app.json --noEmit`: OK.
