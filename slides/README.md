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

El PDF y el PPTX están adjuntos al código, por lo que, una vez publicado el
commit, la página pública del PDF en GitHub ya permite cumplir el requisito de
acceso a las slides:

```text
https://github.com/titiantonio/sindicato-intelligence/blob/main/slides/sindicato_intelligence_tfm.pdf
```

GitHub Pages es una opción adicional para visualizar la variante HTML. Todavía
no está habilitado: la URL prevista devolvía HTTP 404 el 26/07/2026. Para
publicarla:

1. abre `Settings > Pages`;
2. selecciona despliegue desde una rama;
3. elige `main` y la carpeta raíz;
4. guarda la configuración;
5. comprueba:

```text
https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html
```

No añadas ninguna de las URL al README hasta verificarla desde una ventana
privada después de publicar el commit.

## Verificación realizada

- 10 slides exportadas.
- PDF verificado con 10 páginas y formato 16:9.
- HTML verificado con 10 slides, JavaScript válido y assets presentes.
- 10 slides renderizadas mediante PowerPoint.
- inspección visual individual.
- 0 objetos fuera del lienzo según el layout exportado.
- 10 bloques de notas con fuentes.
- capturas embebidas en PowerPoint.

El renderer gráfico de Artifact Tool no pudo usarse en este equipo por una incompatibilidad Vulkan; la exportación editable se realizó con Artifact Tool y la validación visual se hizo con PowerPoint instalado.
