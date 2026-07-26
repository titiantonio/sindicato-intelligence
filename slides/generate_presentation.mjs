import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const OUT = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.dirname(OUT);
const ASSETS = path.join(OUT, "assets");
const RENDER = path.join(ROOT, "tmp", "slides_tfm");

const C = {
  canvas: "#FBFCFD",
  ink: "#0A0F18",
  muted: "#576071",
  panel: "#ECEFF2",
  rule: "#B8C0CA",
  accent: "#0D766E",
  accentSoft: "#D9F0EB",
  blue: "#3D8DFF",
  blueSoft: "#DCEBFF",
  navy: "#0B1323",
  orange: "#C65D32",
  orangeSoft: "#F7E1D7",
  white: "#FFFFFF",
  good: "#12805C",
};

const presentation = Presentation.create({
  slideSize: { width: 1280, height: 720 },
});

function addShape(slide, geometry, position, fill, lineFill = "none", lineWidth = 0, name) {
  return slide.shapes.add({
    geometry,
    name,
    position,
    fill,
    line: { style: "solid", fill: lineFill, width: lineWidth },
  });
}

function addText(slide, text, position, options = {}) {
  const shape = addShape(
    slide,
    "textbox",
    position,
    "none",
    "none",
    0,
    options.name,
  );
  shape.text = text;
  shape.text.style = {
    fontSize: options.fontSize ?? 24,
    typeface: options.typeface ?? "Arial",
    color: options.color ?? C.ink,
    bold: options.bold ?? false,
    alignment: options.alignment ?? "left",
    verticalAlignment: options.verticalAlignment ?? "top",
    autoFit: options.autoFit ?? "shrinkText",
    insets: options.insets ?? { top: 0, right: 0, bottom: 0, left: 0 },
  };
  return shape;
}

function addSlideTitle(slide, title, number, eyebrow = "SINDICATO INTELLIGENCE") {
  addText(slide, eyebrow, { left: 56, top: 34, width: 450, height: 24 }, {
    fontSize: 13,
    bold: true,
    color: C.accent,
    name: `eyebrow-${number}`,
  });
  addText(slide, title, { left: 56, top: 70, width: 1168, height: 78 }, {
    fontSize: 43,
    bold: true,
    color: C.ink,
    name: `title-${number}`,
  });
  addShape(slide, "rect", { left: 56, top: 158, width: 1168, height: 1 }, C.rule);
}

function addFooter(slide, number) {
  addText(slide, String(number).padStart(2, "0"), {
    left: 1180,
    top: 671,
    width: 44,
    height: 20,
  }, {
    fontSize: 12,
    color: C.muted,
    alignment: "right",
    name: `footer-${number}`,
  });
}

function addNotes(slide, talkTrack, sources) {
  slide.speakerNotes.textFrame.setText(
    `${talkTrack}\n\n[Sources]\n${sources.map((source) => `- ${source}`).join("\n")}\n[/Sources]`,
  );
}

async function imageBytes(fileName) {
  const bytes = await fs.readFile(path.join(ASSETS, fileName));
  return bytes.buffer.slice(bytes.byteOffset, bytes.byteOffset + bytes.byteLength);
}

function addScreenshot(slide, bytes, alt, position, crop) {
  addShape(slide, "roundRect", {
    left: position.left - 8,
    top: position.top - 8,
    width: position.width + 16,
    height: position.height + 16,
  }, C.navy, C.navy, 1);
  slide.images.add({
    blob: bytes,
    contentType: "image/png",
    alt,
    fit: "cover",
    position,
    crop,
    geometry: "roundRect",
    borderRadius: 12,
  });
}

const dashboardBytes = await imageBytes("dashboard.png");
const eventsBytes = await imageBytes("events.png");
const settingsBytes = await imageBytes("settings.png");

// 01 — Portada, adaptación del layout Codex Grid slide-01.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addText(slide, "TFM · INTELIGENCIA APLICADA", {
    left: 56, top: 48, width: 540, height: 30,
  }, { fontSize: 16, bold: true, color: C.accent, name: "cover-eyebrow" });
  addText(slide, "Sindicato\nIntelligence", {
    left: 56, top: 170, width: 760, height: 220,
  }, {
    fontSize: 78,
    bold: true,
    color: C.ink,
    autoFit: "none",
    name: "cover-title",
  });
  addText(slide, "La noticia es materia prima.\nEl evento es la unidad de decisión.", {
    left: 58, top: 458, width: 720, height: 92,
  }, { fontSize: 29, color: C.muted, name: "cover-subtitle" });
  addShape(slide, "rect", { left: 924, top: 0, width: 356, height: 720 }, C.navy);
  addShape(slide, "ellipse", { left: 1008, top: 140, width: 176, height: 176 }, C.accentSoft);
  addText(slide, "EVENT", { left: 1008, top: 198, width: 176, height: 44 }, {
    fontSize: 26,
    bold: true,
    color: C.accent,
    alignment: "center",
    verticalAlignment: "middle",
    name: "cover-event",
  });
  addText(slide, "News\n↓\nAnalysis\n↓\nContent\n↓\nPublication", {
    left: 1000, top: 360, width: 190, height: 245,
  }, {
    fontSize: 23,
    color: C.white,
    alignment: "center",
    name: "cover-flow",
  });
  addNotes(
    slide,
    "Presentar el proyecto en una frase y explicar que Event es el centro del dominio.",
    [
      "README.md",
      "docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md",
    ],
  );
}

// 02 — Problema, composición de dos campos.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "Muchos medios pueden contar un solo hecho", 2, "EL PROBLEMA");

  addText(slide, "Seguimiento manual", {
    left: 56, top: 214, width: 430, height: 46,
  }, { fontSize: 28, bold: true });
  addText(slide,
    "Revisar RSS, detectar duplicados, clasificar y redactar consume tiempo y fragmenta la visión del asunto.",
    { left: 56, top: 278, width: 440, height: 176 },
    { fontSize: 24, color: C.muted },
  );
  addText(slide, "La consecuencia", {
    left: 56, top: 510, width: 430, height: 40,
  }, { fontSize: 19, bold: true, color: C.orange });
  addText(slide, "5 noticias similares pueden acabar en 5 decisiones y 5 publicaciones.", {
    left: 56, top: 554, width: 465, height: 76,
  }, { fontSize: 23, color: C.ink });

  // Flechas detrás de los nodos.
  addShape(slide, "rightArrow", { left: 712, top: 311, width: 96, height: 38 }, C.rule);
  addShape(slide, "rightArrow", { left: 1010, top: 311, width: 92, height: 38 }, C.accent);

  const sourceYs = [210, 280, 350, 420, 490];
  sourceYs.forEach((top, index) => {
    addShape(slide, "roundRect", { left: 586, top, width: 150, height: 48 }, C.panel);
    addText(slide, `Medio ${index + 1}`, { left: 600, top: top + 11, width: 122, height: 28 }, {
      fontSize: 18,
      alignment: "center",
      bold: index === 0,
    });
  });
  addShape(slide, "roundRect", { left: 812, top: 260, width: 200, height: 142 }, C.orangeSoft);
  addText(slide, "Duplicados\n+ contexto disperso", {
    left: 834, top: 298, width: 156, height: 72,
  }, { fontSize: 23, bold: true, alignment: "center" });
  addShape(slide, "ellipse", { left: 1096, top: 255, width: 144, height: 144 }, C.accentSoft);
  addText(slide, "1\nEVENTO", { left: 1110, top: 286, width: 116, height: 82 }, {
    fontSize: 27,
    bold: true,
    color: C.accent,
    alignment: "center",
  });
  addText(slide, "La plataforma consolida antes de decidir.", {
    left: 760, top: 522, width: 480, height: 50,
  }, { fontSize: 27, bold: true, color: C.accent, alignment: "right" });
  addFooter(slide, 2);
  addNotes(
    slide,
    "Explicar el coste del seguimiento manual y el riesgo de duplicar decisiones editoriales.",
    [
      "Documentacion-TFM-Fundae-1.pdf",
      "docs/Documentacion Proyecto/Documento 01 - Visión y Arquitectura General del Sistema.md",
    ],
  );
}

// Modelo de dominio, invocado como slide 04.
function addDomainSlide(number) {
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "El flujo transforma noticias en decisiones publicables", number, "MODELO DE DOMINIO");

  const xs = [64, 286, 524, 774, 1018];
  const widths = [150, 174, 180, 180, 194];
  const labels = ["NEWS", "EVENT", "ANALYSIS", "CONTENT", "PUBLICATION"];
  const descriptions = [
    "Materia prima\ncapturada",
    "Hecho consolidado\naggregate root",
    "Lectura IA\ncon contexto",
    "Borrador sujeto\na revisión",
    "Envío aprobado\ny trazable",
  ];

  for (let index = 0; index < xs.length - 1; index += 1) {
    addShape(slide, "rightArrow", {
      left: xs[index] + widths[index] + 10,
      top: 326,
      width: xs[index + 1] - (xs[index] + widths[index]) - 20,
      height: 32,
    }, index === 0 ? C.accent : C.rule);
  }

  labels.forEach((label, index) => {
    const isEvent = label === "EVENT";
    if (label === "PUBLICATION") {
      addShape(slide, "rect", {
        left: xs[index] - 10,
        top: 230,
        width: widths[index] + 24,
        height: 330,
      }, C.canvas);
    }
    addShape(slide, isEvent ? "ellipse" : "roundRect", {
      left: xs[index],
      top: isEvent ? 250 : 270,
      width: widths[index],
      height: isEvent ? 180 : 140,
    }, isEvent ? C.accent : C.panel);
    addText(slide, label, {
      left: xs[index] + 12,
      top: isEvent ? 304 : 306,
      width: widths[index] - 24,
      height: 44,
    }, {
      fontSize: isEvent ? 29 : 22,
      bold: true,
      color: isEvent ? C.white : C.ink,
      alignment: "center",
      verticalAlignment: "middle",
    });
    addText(slide, descriptions[index], {
      left: xs[index] - 8,
      top: 464,
      width: widths[index] + 16,
      height: 70,
    }, { fontSize: 18, color: C.muted, alignment: "center" });
  });
  addText(slide, "La IA propone. El dominio valida. Una persona aprueba.", {
    left: 240, top: 586, width: 800, height: 42,
  }, { fontSize: 28, bold: true, color: C.accent, alignment: "center" });
  addFooter(slide, number);
  addNotes(
    slide,
    "Recorrer News, Event, Analysis, Content y Publication. Reforzar que Event es el aggregate root principal.",
    [
      "docs/Documentacion Proyecto/Documento 17 – Modelo de Dominio (DDD).md",
      "docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md",
    ],
  );
}

// Arquitectura, invocada como slide 03.
function addArchitectureSlide(number) {
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "Spring Boot concentra el dominio y limita cada integración", number, "ARQUITECTURA");

  // Conectores primero.
  addShape(slide, "rightArrow", { left: 272, top: 342, width: 70, height: 34 }, C.rule);
  addShape(slide, "rightArrow", { left: 900, top: 342, width: 70, height: 34 }, C.rule);

  addShape(slide, "roundRect", { left: 58, top: 224, width: 214, height: 270 }, C.blueSoft);
  addText(slide, "ENTRADA", { left: 82, top: 250, width: 166, height: 30 }, {
    fontSize: 16, bold: true, color: C.blue, alignment: "center",
  });
  addText(slide, "Fuentes RSS\n\nn8n · WF-01\nCaptura XML\n\nPOST /news/bulk", {
    left: 80, top: 300, width: 170, height: 166,
  }, { fontSize: 22, alignment: "center" });

  addShape(slide, "roundRect", { left: 342, top: 198, width: 558, height: 330 }, C.navy);
  addText(slide, "SPRING BOOT · MONOLITO MODULAR", {
    left: 378, top: 226, width: 486, height: 32,
  }, { fontSize: 18, bold: true, color: C.accentSoft, alignment: "center" });
  addText(slide, "domain  ·  application  ·  infrastructure  ·  api", {
    left: 382, top: 274, width: 478, height: 36,
  }, { fontSize: 20, color: C.white, alignment: "center" });
  addShape(slide, "rect", { left: 392, top: 328, width: 458, height: 1 }, "#526076");
  addText(slide, "Reglas de negocio\nCasos de uso\nJWT y roles\nIA y validación\nAutomatizaciones\nTelegram y auditoría", {
    left: 400, top: 354, width: 442, height: 126,
  }, { fontSize: 21, color: C.white, alignment: "center" });

  addShape(slide, "roundRect", { left: 970, top: 224, width: 252, height: 270 }, C.accentSoft);
  addText(slide, "SALIDAS", { left: 996, top: 250, width: 200, height: 30 }, {
    fontSize: 16, bold: true, color: C.accent, alignment: "center",
  });
  addText(slide, "Angular\nPostgreSQL\nGemini opcional\nTelegram\nMailHog", {
    left: 998, top: 302, width: 196, height: 168,
  }, { fontSize: 22, alignment: "center" });

  addText(slide, "DDD · Clean Architecture · PostgreSQL + Flyway", {
    left: 342, top: 574, width: 558, height: 38,
  }, { fontSize: 23, bold: true, color: C.accent, alignment: "center" });
  addFooter(slide, number);
  addNotes(
    slide,
    "Explicar que n8n solo captura. Toda regla, scheduler, IA, seguridad y publicación vive en Spring Boot.",
    [
      "docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md",
      "docs/Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md",
    ],
  );
}

addArchitectureSlide(3);
addDomainSlide(4);

// 05 — Producto real, adaptación del half-image Codex Grid slide-08.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "El dashboard convierte datos dispersos en prioridades", 5, "PRODUCTO OPERATIVO");
  addText(slide, "Una lectura rápida para decidir qué necesita atención hoy.", {
    left: 56, top: 210, width: 382, height: 72,
  }, { fontSize: 27, bold: true });
  addText(slide,
    "• métricas del día\n• avisos editoriales\n• eventos críticos\n• automatizaciones manuales\n• acceso por rol",
    { left: 56, top: 320, width: 350, height: 190 },
    { fontSize: 23, color: C.muted },
  );
  addText(slide, "La UI no decide: expone contexto y acciones de dominio.", {
    left: 56, top: 550, width: 380, height: 70,
  }, { fontSize: 21, bold: true, color: C.accent });
  addScreenshot(
    slide,
    dashboardBytes,
    "Dashboard actual de Sindicato Intelligence",
    { left: 466, top: 198, width: 758, height: 426 },
    { left: 0, top: 0, right: 0, bottom: 0 },
  );
  addFooter(slide, 5);
  addNotes(
    slide,
    "Usar esta slide como transición a la demostración real. Señalar dashboard, avisos y eventos prioritarios.",
    [
      "slides/assets/dashboard.png (captura local 25/07/2026)",
      "frontend/src/app/features/dashboard",
    ],
  );
}

// 06 — Dos pantallas reales.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "Eventos y configuración cubren decisión y operación", 6, "BACKOFFICE");

  addScreenshot(
    slide,
    eventsBytes,
    "Listado actual de eventos",
    { left: 56, top: 204, width: 562, height: 316 },
    { left: 0, top: 0, right: 0, bottom: 0 },
  );
  addScreenshot(
    slide,
    settingsBytes,
    "Pantalla actual de métricas IA",
    { left: 662, top: 204, width: 562, height: 316 },
    { left: 0, top: 0, right: 0, bottom: 0 },
  );

  addText(slide, "EVENTOS", { left: 56, top: 548, width: 200, height: 30 }, {
    fontSize: 17, bold: true, color: C.accent,
  });
  addText(slide, "Filtros, impacto, noticias vinculadas y estado editorial.", {
    left: 56, top: 584, width: 520, height: 48,
  }, { fontSize: 21 });

  addText(slide, "SETTINGS", { left: 662, top: 548, width: 200, height: 30 }, {
    fontSize: 17, bold: true, color: C.blue,
  });
  addText(slide, "Métricas IA, prompts, automatizaciones y Telegram.", {
    left: 662, top: 584, width: 520, height: 48,
  }, { fontSize: 21 });
  addFooter(slide, 6);
  addNotes(
    slide,
    "Contrastar la pantalla editorial de Eventos con el centro ADMIN de settings.",
    [
      "slides/assets/events.png (captura local 25/07/2026)",
      "slides/assets/settings.png (captura local 25/07/2026)",
    ],
  );
}

// 07 — Automatización e IA, adaptación del timeline de tres hitos Codex Grid slide-18.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "La IA acelera el flujo sin sustituir la revisión", 7, "AUTOMATIZACIÓN E IA");

  const phases = [
    {
      left: 56,
      fill: C.blueSoft,
      label: "01 · CAPTURA",
      body: "n8n · WF-01\n\nRSS/XML\nNormalización\nAlta masiva",
      foot: "Entrada externa",
    },
    {
      left: 453,
      fill: C.panel,
      label: "02 · INTELIGENCIA",
      body: "Spring · WF-02 a WF-04\n\nClasificar\nAgrupar eventos\nAnalizar",
      foot: "Reglas + validación",
    },
    {
      left: 850,
      fill: C.accentSoft,
      label: "03 · DECISIÓN",
      body: "Spring · WF-05/06\n\nGenerar\nRevisar y aprobar\nPublicar",
      foot: "Control humano",
    },
  ];

  addShape(slide, "straightConnector1", {
    left: 80, top: 574, width: 1098, height: 1,
  }, "none", C.rule, 2);

  phases.forEach((phase, index) => {
    addShape(slide, "roundRect", { left: phase.left, top: 205, width: 340, height: 300 }, phase.fill);
    addText(slide, phase.label, {
      left: phase.left + 28, top: 236, width: 284, height: 32,
    }, { fontSize: 17, bold: true, color: index === 1 ? C.ink : (index === 0 ? C.blue : C.accent) });
    addText(slide, phase.body, {
      left: phase.left + 28, top: 296, width: 284, height: 172,
    }, { fontSize: 23, color: C.ink });
    addShape(slide, "ellipse", { left: phase.left + 10, top: 565, width: 18, height: 18 }, C.ink);
    addText(slide, phase.foot, {
      left: phase.left + 38, top: 553, width: 270, height: 38,
    }, { fontSize: 18, bold: true });
  });

  addText(slide, "Prompts versionados · respuesta JSON validada · métricas por operación", {
    left: 196, top: 630, width: 888, height: 34,
  }, { fontSize: 21, color: C.muted, alignment: "center" });
  addFooter(slide, 7);
  addNotes(
    slide,
    "Diferenciar captura externa, inteligencia interna y decisión humana. Mencionar proveedor determinista y Gemini configurable.",
    [
      "docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md",
      "docs/Documentacion Proyecto/2026_06_27_flujo_completo_wf_01_wf_06.md",
    ],
  );
}

// 08 — Seguridad y gobierno editorial.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.navy;
  addText(slide, "SEGURIDAD Y GOBIERNO", { left: 56, top: 42, width: 520, height: 28 }, {
    fontSize: 15, bold: true, color: C.accentSoft,
  });
  addText(slide, "Automatizar no significa perder el control", {
    left: 56, top: 86, width: 1168, height: 72,
  }, { fontSize: 43, bold: true, color: C.white });
  addShape(slide, "rect", { left: 56, top: 176, width: 1168, height: 1 }, "#41506A");

  addText(slide, "CONTROLES TÉCNICOS", {
    left: 56, top: 222, width: 430, height: 34,
  }, { fontSize: 18, bold: true, color: C.accentSoft });
  addText(slide,
    "JWT de 15 min\nRefresh de 7 días\nRoles ADMIN / EDITOR\nRate limiting de autenticación\nSecretos fuera de Git\nAuditoría de acciones",
    { left: 56, top: 282, width: 450, height: 256 },
    { fontSize: 25, color: C.white },
  );

  addShape(slide, "roundRect", { left: 592, top: 214, width: 632, height: 374 }, "#151F33");
  addText(slide, "REGLA EDITORIAL", {
    left: 630, top: 250, width: 552, height: 32,
  }, { fontSize: 18, bold: true, color: C.orangeSoft, alignment: "center" });
  addText(slide, "GENERATED", { left: 626, top: 322, width: 144, height: 48 }, {
    fontSize: 18, bold: true, color: C.white, alignment: "center", verticalAlignment: "middle",
  });
  addShape(slide, "rightArrow", { left: 770, top: 330, width: 70, height: 30 }, C.rule);
  addText(slide, "REVIEW", { left: 836, top: 322, width: 136, height: 48 }, {
    fontSize: 18, bold: true, color: C.white, alignment: "center", verticalAlignment: "middle",
  });
  addShape(slide, "rightArrow", { left: 972, top: 330, width: 70, height: 30 }, C.accent);
  addShape(slide, "roundRect", { left: 1042, top: 310, width: 152, height: 72 }, C.accent);
  addText(slide, "APPROVED", { left: 1054, top: 329, width: 128, height: 36 }, {
    fontSize: 18, bold: true, color: C.white, alignment: "center",
  });
  addText(slide, "Solo después puede publicarse", {
    left: 686, top: 424, width: 442, height: 50,
  }, { fontSize: 27, bold: true, color: C.accentSoft, alignment: "center" });
  addText(slide, "La IA nunca aprueba ni publica por sí sola.", {
    left: 670, top: 498, width: 474, height: 46,
  }, { fontSize: 21, color: C.white, alignment: "center" });
  addText(slide, "08", { left: 1180, top: 671, width: 44, height: 20 }, {
    fontSize: 12, color: C.rule, alignment: "right",
  });
  addNotes(
    slide,
    "Explicar JWT, roles y la regla de aprobación. Recalcar que el backend aplica autorización y el contenido editado vuelve a revisión.",
    [
      "docs/Documentacion Proyecto/Documento 13 - Seguridad y Roles.md",
      "backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java",
    ],
  );
}

// 09 — Evidencia de calidad, adaptación del metric-led Codex Grid slide-19.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addSlideTitle(slide, "La entrega se apoya en evidencia reproducible", 9, "CALIDAD");
  addText(slide, "Baterías automatizadas + build + validadores + Docker.", {
    left: 56, top: 180, width: 730, height: 42,
  }, { fontSize: 25, color: C.muted });

  const metrics = [
    { left: 56, value: "347", label: "pruebas backend", fill: C.accentSoft, color: C.accent },
    { left: 453, value: "163", label: "pruebas frontend", fill: C.blueSoft, color: C.blue },
    { left: 850, value: "16", label: "pruebas E2E mock", fill: C.orangeSoft, color: C.orange },
  ];
  metrics.forEach((metric) => {
    addShape(slide, "roundRect", { left: metric.left, top: 258, width: 340, height: 228 }, metric.fill);
    addText(slide, metric.value, {
      left: metric.left + 28, top: 292, width: 284, height: 100,
    }, { fontSize: 68, bold: true, color: metric.color, verticalAlignment: "bottom" });
    addText(slide, metric.label, {
      left: metric.left + 30, top: 408, width: 280, height: 44,
    }, { fontSize: 23, bold: true });
  });

  addText(slide, "✓ Angular production build", {
    left: 56, top: 548, width: 340, height: 34,
  }, { fontSize: 20, bold: true, color: C.good });
  addText(slide, "✓ 0 vulnerabilidades de producción", {
    left: 453, top: 548, width: 390, height: 34,
  }, { fontSize: 20, bold: true, color: C.good });
  addText(slide, "✓ Compose, PowerShell y WF-01", {
    left: 850, top: 548, width: 374, height: 34,
  }, { fontSize: 20, bold: true, color: C.good });
  addShape(slide, "rect", { left: 56, top: 612, width: 1168, height: 1 }, C.rule);
  addText(slide, "Ejecución de evaluación", { left: 56, top: 635, width: 260, height: 26 }, {
    fontSize: 16, bold: true, color: C.muted,
  });
  addText(slide, ".\\tfm-start.ps1   →   .\\tfm-check.ps1", {
    left: 340, top: 627, width: 600, height: 38,
  }, { fontSize: 23, bold: true, color: C.ink, alignment: "center" });
  addFooter(slide, 9);
  addNotes(
    slide,
    "Presentar resultados verificados, el audit de producción y el arranque reproducible. No afirmar cobertura porcentual no medida.",
    [
      "docs/Documentacion Proyecto/Documento 15 - Plan de Pruebas.md",
      "docs/Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md",
    ],
  );
}

// 10 — Cierre, adaptación del stacked-text Codex Grid slide-26.
{
  const slide = presentation.slides.add();
  slide.background.fill = C.canvas;
  addText(slide, "CONCLUSIÓN", { left: 56, top: 48, width: 260, height: 28 }, {
    fontSize: 15, bold: true, color: C.accent,
  });
  addText(slide, "Automatizar lo repetitivo.\nMantener humana la decisión.", {
    left: 56, top: 166, width: 1020, height: 210,
  }, {
    fontSize: 66,
    bold: true,
    color: C.ink,
    autoFit: "none",
    name: "closing-title",
  });
  addShape(slide, "rect", { left: 56, top: 420, width: 720, height: 4 }, C.accent);
  addText(slide, "Sindicato Intelligence convierte noticias dispersas en eventos trazables, análisis consolidados y contenido listo para revisión.", {
    left: 56, top: 474, width: 820, height: 100,
  }, { fontSize: 27, color: C.muted });
  addShape(slide, "roundRect", { left: 940, top: 456, width: 284, height: 150 }, C.navy);
  addText(slide, "MVP ejecutable\nDocker · API · Backoffice\nIA · Telegram · Auditoría", {
    left: 968, top: 486, width: 228, height: 96,
  }, { fontSize: 20, bold: true, color: C.white, alignment: "center" });
  addText(slide, "github.com/titiantonio/sindicato-intelligence", {
    left: 56, top: 644, width: 620, height: 24,
  }, { fontSize: 15, color: C.accent });
  addNotes(
    slide,
    "Cerrar resolviendo el problema inicial: menos trabajo repetitivo, un evento por hecho y revisión humana antes de publicar.",
    [
      "README.md",
      "docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md",
    ],
  );
}

await fs.mkdir(OUT, { recursive: true });

const pptx = await PresentationFile.exportPptx(presentation);
await pptx.save(path.join(OUT, "sindicato_intelligence_tfm.pptx"));

await fs.mkdir(RENDER, { recursive: true });
const snapshot = await presentation.inspect({
  kind: "slide,textbox,shape,image,notes,layout",
  maxChars: 20000,
});
await fs.writeFile(path.join(RENDER, "presentation-inspect.ndjson"), snapshot.ndjson);

console.log(`Presentación creada: ${presentation.slides.items.length} slides. El render visual se valida mediante PowerPoint debido a la incompatibilidad Vulkan del renderer local.`);
