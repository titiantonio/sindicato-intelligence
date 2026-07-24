# Comprobacion evento 17 PDF BOJA

Fecha: 2026-07-24

## Objetivo

Comprobar si el fallo de `WF04_ANALYSIS` del evento 17 estaba relacionado con que la noticia 44 es una resolucion oficial enlazada como PDF del BOJA.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a Fase 12 del Documento 30: automatizaciones internas, IA, enlaces relevantes y publicacion Telegram.

## Datos comprobados

- `event_id=17`: evento `OPEN`, categoria `INTERINOS`, importancia `CRITICAL`.
- `news_id=44`: unica noticia asociada al evento.
- URL de la noticia: `http://www.juntadeandalucia.es/boja/2026/214001/BOJA26-214001-00002-9998-01_00341229.pdf`.
- `content` de la noticia vacio.
- `summary` con metadatos BOJA de 227 caracteres.
- Sin analisis ni contenido generado para el evento en el momento de la comprobacion.

## Decisiones tomadas

- El fallo `RECITATION` no se debe a que el PDF se adjunte a Gemini: WF-04 no adjunta PDFs ni binarios al proveedor IA.
- WF-04 solo envia metadatos, resumen, URL y contenido textual capturado si existe.
- Se corrige WF-05 para detectar la URL principal de la noticia como enlace relevante cuando ya es un PDF/documento oficial permitido.
- La publicacion automatica generada sigue incluyendo enlaces relevantes en el mensaje; los adjuntos binarios reales existen solo en el flujo de publicaciones manuales.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/HttpRelevantContentLinkExtractor.java`
- `backend/src/test/java/es/sindicato/intelligence/content/infrastructure/HttpRelevantContentLinkExtractorTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Pruebas y verificaciones

- `mvn "-Dtest=HttpRelevantContentLinkExtractorTest,GenerateContentUseCaseTest" test`: OK, 10 tests.
