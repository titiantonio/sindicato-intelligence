# Slides del TFM

## Archivos

- `sindicato_intelligence_tfm.pptx`: presentación editable.
- `sindicato_intelligence_tfm.pdf`: versión portátil.
- `tfm_presentacion.html`: presentación web para GitHub Pages.
- `sindicato_intelligence_tfm_preview.png`: vista general.
- `assets/`: capturas actuales usadas en PowerPoint y HTML.
- `generate_presentation.mjs`: fuente reproducible del PowerPoint; requiere el
  runtime de artefactos incluido en Codex.

## Presentación HTML

Puede abrirse localmente con:

```powershell
Start-Process .\slides\tfm_presentacion.html
```

Controles:

- flechas, espacio o Page Down: avanzar;
- flecha izquierda o Page Up: retroceder;
- Home / End: inicio o final;
- `F`: pantalla completa.

## Publicación con GitHub Pages

El PDF y el PPTX están adjuntos al código. La página pública del PDF en GitHub
está disponible en:

```text
https://github.com/titiantonio/sindicato-intelligence/blob/main/slides/sindicato_intelligence_tfm.pdf
```

La variante HTML está publicada mediante GitHub Pages:

```text
https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html
```

Ambas URL devolvían HTTP 200 y la presentación web fue comprobada por el autor
el 26/07/2026.

## Verificación realizada

- 10 slides exportadas.
- PDF verificado con 10 páginas y formato 16:9.
- HTML verificado con 10 slides, JavaScript válido y assets presentes.
- 10 slides renderizadas mediante PowerPoint.
- inspección visual individual.
- orden narrativo verificado: arquitectura en la slide 03 y modelo de dominio
  en la slide 04.
- 0 objetos fuera del lienzo según el layout exportado.
- 10 bloques de notas con fuentes.
- capturas embebidas en PowerPoint.

El renderer gráfico de Artifact Tool no pudo usarse en este equipo por una incompatibilidad Vulkan; la exportación editable se realizó con Artifact Tool y la validación visual se hizo con PowerPoint instalado.
