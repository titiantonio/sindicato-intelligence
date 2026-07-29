# Contexto rápido para agentes

## Propósito

Plataforma interna de inteligencia informativa para un sindicato de docentes de
Andalucía. Captura noticias educativas, las clasifica, las agrupa en eventos,
genera análisis y contenido editorial y permite su revisión y publicación.

## Flujo vigente

```text
RSS/XML
  -> WF-01 n8n
  -> Spring Boot / PostgreSQL
  -> WF-02 clasificación IA
  -> WF-03 detección de eventos
  -> WF-04 análisis IA
  -> WF-05 contenido bajo demanda
  -> revisión humana
  -> WF-06 publicación Telegram
```

`Event` es el aggregate root principal. No se genera ni publica contenido desde
noticias individuales. `WF-01` es el único workflow que permanece en n8n;
`WF-02` a `WF-06` residen en Spring Boot.

## Arquitectura y tecnología

- Backend: Java 21, Spring Boot 3.x, DDD, Clean Architecture y monolito modular.
- Base package: `es.sindicato.intelligence`.
- Frontend: Angular, backoffice responsive y E2E con Playwright.
- Persistencia: PostgreSQL y migraciones Flyway.
- Infraestructura local: Docker Compose, MailHog y n8n.
- Seguridad: JWT, roles `ADMIN` y `EDITOR`, auditoría y cambio obligatorio de
  contraseña temporal.

Módulos principales: `source`, `news`, `classification`, `event`, `analysis`,
`content`, `publication`, `user`, `auth`, `automation`, `ai`, `audit`,
`dashboard`, `health` y `core`.

## Estado operativo a 26/07/2026

- Fases 0 a 12 y Sprints 10 a 12 completados.
- Sprint 13 de calidad E2E implementado con suites mockeadas y opt-in real.
- Sprint 14 de consolidación visual y accesibilidad completado.
- Sprint 15 de preparación de entrega TFM completado.
- Versiones actuales: backend `0.0.121-SNAPSHOT` y frontend `0.0.51`.
- Repositorio público verificado el 26/07/2026.
- `Secret Protection` y `Push protection` activos en GitHub desde el
  26/07/2026.
- Fecha límite efectiva confirmada por el autor: 24/08/2026.
- Slides publicadas y verificadas en GitHub Pages el 26/07/2026:
  `https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html`.
- Vídeo adjuntado mediante el formulario oficial de entrega.
- Enlaces y credenciales de demostración validados antes del envío.
- TFM entregado mediante el formulario oficial el 29/07/2026.
- Checklist operativa de cierre disponible en
  `docs/Documentacion_Final/2026_07_26_checklist_cierre_entrega_tfm.md`.

## Documentos principales

- `Documento 17`: modelo de dominio DDD.
- `Documento 18`: estructura Spring Boot.
- `Documento 19`: casos de uso.
- `Documento 20`: ERD y Flyway.
- `Documento 21`: convenciones de desarrollo.
- `Documento 23`: catálogo de prompts IA.
- `Documento 30`: MVP técnico ejecutable y secuencia de fases.
- `Documento 31`: backlog operativo y control de avance.

La entrada documental recomendada para evaluadores es
`docs/indice_documentacion.md`.
