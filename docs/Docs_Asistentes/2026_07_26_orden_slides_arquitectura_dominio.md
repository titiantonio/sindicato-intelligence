# Orden de slides de arquitectura y dominio

## Fecha

26/07/2026.

## Objetivo

Sincronizar el orden de la presentación con la narración prevista para el vídeo
del TFM.

## Contexto

El guion introduce primero la arquitectura técnica y después el flujo del
modelo de dominio. La presentación mostraba esos contenidos en el orden
contrario.

## Fase MVP

Cierre transversal de las fases 0 a 12. Tarea T15.4 del Sprint 15 en el
Documento 31.

## Archivos modificados

- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion_Final/2026_07_25_guion_video_tfm.md`.
- `slides/README.md`.
- `slides/generate_presentation.mjs`.
- `slides/sindicato_intelligence_tfm.pptx`.
- `slides/sindicato_intelligence_tfm.pdf`.
- `slides/sindicato_intelligence_tfm_preview.png`.
- `slides/tfm_presentacion.html`.
- Este registro.

## Decisiones

- Situar arquitectura como slide 03.
- Situar modelo de dominio como slide 04.
- Mantener el resto de la presentación sin cambios.
- Indicar en el guion el avance explícito de la slide 03 a la 04.
- Regenerar todos los formatos para evitar divergencias.
- No modificar versiones de aplicación porque el cambio afecta únicamente a
  materiales de presentación.

## Pruebas o verificaciones

- PPTX generado con Artifact Tool: 10 slides.
- Inspección estructural: slide 03 `ARQUITECTURA`, slide 04
  `MODELO DE DOMINIO`, 10 notas y 10 bloques de fuentes.
- Layout inspeccionado: 58 objetos con caja y 0 fuera de 1280 × 720.
- PowerPoint: 10 slides renderizadas a PNG.
- Inspección visual individual de las slides 03 y 04.
- Vista previa completa regenerada e inspeccionada.
- PDF: 10 páginas; página 3 de arquitectura y página 4 de modelo de dominio.
- HTML: 10 secciones; arquitectura antes que dominio y numeración 03/04.
- `git diff --check`: correcto.

