# Auditoría final de requisitos del TFM

## Fecha

26/07/2026.

## Objetivo

Cerrar la revisión integral del proyecto frente a
`Documentacion-TFM-Fundae-1.pdf`, corregir contradicciones entre los artefactos
de entrega y volver a verificar que código, documentación, presentación y
entorno reproducible están preparados para su publicación.

## Contexto

- La fecha límite efectiva confirmada por el autor es el 24/08/2026.
- El repositorio GitHub ya es público.
- El vídeo se grabará sobre el entorno de desarrollo existente, que contiene
  datos útiles para la demostración.
- La grabación, publicación del vídeo, subida de los cambios locales y envío del
  formulario siguen siendo acciones externas pendientes.

## Fase MVP

Cierre transversal de las fases 0 a 12. Sprint 15 del Documento 31.

## Archivos modificados

- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md`.
- `docs/Documentacion_Final/2026_07_26_checklist_cierre_entrega_tfm.md`.
- `slides/README.md`.
- `slides/generate_presentation.mjs`.
- `slides/tfm_presentacion.html`.
- `slides/sindicato_intelligence_tfm.pptx`.
- `slides/sindicato_intelligence_tfm.pdf`.
- `slides/sindicato_intelligence_tfm_preview.png`.
- `frontend/e2e/visual-system.mock.spec.ts`.
- Este registro de intervención.

## Decisiones

- Interpretar literalmente el requisito de slides: el PDF incorporado al
  repositorio público es un documento adjunto válido y puede enlazarse mediante
  la URL pública de GitHub. GitHub Pages es opcional.
- Mantener como obligatorios la publicación del repositorio actualizado, el
  vídeo con voz y captura, sus URL públicas y el formulario de entrega.
- Mantener el despliegue público como recomendado, no obligatorio.
- Usar el entorno de desarrollo con datos para el vídeo, sin ejecutar
  `tfm-reset.ps1`, sin IA externa y con Telegram deshabilitado.
- Corregir la etiqueta visual del flujo para mostrar `WF-02` a `WF-04` en
  Spring Boot.
- Ampliar a 60 segundos el presupuesto del único recorrido E2E que navega por
  13 rutas. No se modificaron ni relajaron sus aserciones.
- Excluir de la entrega archivos de trabajo de generación de slides,
  dependencias descargadas, logs, temporales, artefactos de build, secretos y
  volcados de base de datos.
- Mantener las versiones de entrega ya actualizadas:
  `0.0.120-SNAPSHOT` en backend y `0.0.48` en frontend.
- No realizar commit ni push sin autorización expresa del autor.

## Pruebas y verificaciones

- PDF de requisitos revisado íntegramente y contrastado con una matriz de
  cumplimiento.
- Repositorio público verificado sin sesión.
- Presentación validada con 10 slides, 10 bloques de notas y fuentes, sin
  elementos fuera del lienzo.
- PPTX renderizado con PowerPoint e inspeccionado slide a slide.
- PDF renderizado e inspeccionado: 10 páginas.
- HTML validado: 10 slides y referencias locales existentes.
- Enlaces Markdown locales: 307 archivos revisados, 36 enlaces locales y
  0 rotos.
- Backend Maven: 103 suites, 347 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Frontend unitario: 163 pruebas, 0 fallos.
- Frontend build de producción: correcto.
- Playwright mock: 16 pruebas, 0 fallos.
- Auditoría npm de producción: 0 vulnerabilidades.
- Auditoría npm completa: 9 vulnerabilidades altas restringidas a la cadena de
  desarrollo de Karma; no existe corrección compatible sin sustituir o romper
  el runner actual.
- `docker compose config`: correcto.
- `n8n/validate-workflows.ps1`: `WF-01` válido.
- Todos los scripts PowerShell analizados: sintaxis válida.
- Smoke limpio del stack Docker completo realizado previamente con resultado
  correcto.
- Entorno de desarrollo detenido al terminar, sin eliminar sus volúmenes ni
  datos.
- `git diff --check`: correcto.

## Estado final

El trabajo técnico y documental que puede realizarse localmente queda
completado. El Sprint 15 permanece abierto únicamente por acciones externas:
subir los cambios, comprobar la URL pública del PDF de slides, grabar y publicar
el vídeo, registrar las URL finales y enviar el formulario antes del
24/08/2026.
