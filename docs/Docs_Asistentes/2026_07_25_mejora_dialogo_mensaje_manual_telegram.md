# 2026_07_25_mejora_dialogo_mensaje_manual_telegram

## Fecha

2026-07-25

## Objetivo

Mejorar la apariencia del dialogo de envio de mensaje manual de Telegram en el backoffice Angular.

## Contexto

Intervencion de mantenimiento visual sobre la Fase 11 del MVP, pantalla de Publicaciones, alineada con la tarea T11.8 Publicaciones y el registro operativo del Documento 31.

## Fase MVP

Fase 11: Frontend Angular.

## Archivos modificados

- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.scss`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_07_25_mejora_dialogo_mensaje_manual_telegram.md`

## Decisiones

- Se mantuvo intacta la logica de envio manual, carga de destinos, seleccion de adjuntos y publicacion.
- Se incorporo una cabecera contextual al dialogo y un boton de entrada con icono.
- Se mejoro la jerarquia visual de editor, destinos, adjuntos y acciones usando los tokens existentes del frontend.
- Se ajusto el SCSS para respetar el presupuesto de estilos del componente durante el build.
- Tras revision visual, se retiro el enfoque de dos columnas y se mantuvo un formulario simple y compacto.
- Se elimino el scroll vertical interno del dialogo para evitar que los adjuntos quedaran ocultos.
- Se corrigio la alineacion de checkbox y destinatario en la misma fila.
- Se dejo la toolbar del editor en una sola linea y sin etiquetas visibles `Texto`/`Telegram`.
- Se subio la version del frontend a `0.0.46`.

## Pruebas o verificaciones

- `npm.cmd run build` en `frontend`: OK.
