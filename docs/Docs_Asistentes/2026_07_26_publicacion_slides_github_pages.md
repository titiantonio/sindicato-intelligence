# Publicación de slides en GitHub Pages

## Fecha

26/07/2026.

## Objetivo

Actualizar los enlaces y estados documentales de la entrega después de publicar
el commit de preparación y habilitar la presentación del TFM en GitHub Pages.

## Contexto

- El autor publicó el commit `00e6fd4` en `main`.
- El `HEAD` local y `origin/main` coincidían antes de esta intervención.
- La presentación HTML ya es accesible públicamente.
- El vídeo y el formulario de entrega continúan pendientes.

## Fase MVP

Cierre transversal de las fases 0 a 12. Tarea T15.5 del Sprint 15 en el
Documento 31.

## Archivos modificados

- `README.md`.
- `CHANGELOG.md`.
- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion_Final/2026_07_25_guion_video_tfm.md`.
- `docs/Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md`.
- `docs/Documentacion_Final/2026_07_26_checklist_cierre_entrega_tfm.md`.
- `slides/README.md`.
- Este registro.

## Decisiones

- Usar como enlace principal de slides:
  `https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html`.
- Mantener también documentado el PDF público del repositorio como alternativa.
- Marcar como completadas en el Documento 31 la publicación del commit de
  preparación y la publicación de las slides.
- Mantener abierto el Sprint 15 hasta publicar el vídeo y enviar el formulario.
- No incrementar versiones porque la intervención solo modifica documentación.
- No crear commit ni hacer push sin una petición expresa.

## Pruebas o verificaciones

- GitHub Pages: HTTP 200, 21.314 bytes, título esperado y 10 slides detectadas.
- PDF público: HTTP 200, 511.849 bytes.
- Commit local y remoto antes de los cambios: `00e6fd4`.
- Enlaces y referencias pendientes revisados mediante búsqueda dirigida.
- `git diff --check` ejecutado sin errores.

