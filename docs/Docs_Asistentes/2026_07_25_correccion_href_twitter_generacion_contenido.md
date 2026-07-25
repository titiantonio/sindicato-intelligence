# Fecha

2026-07-25

# Objetivo

Corregir el error al pulsar `Generar primer contenido` en el dashboard cuando una noticia contiene un enlace social malformado como `https://twitter.com/share?text=...` con espacios sin codificar.

# Contexto

La intervencion corresponde a mantenimiento correctivo de Sprint 8, Fase 9 del Documento 30, flujo `WF-05-Generate-Content`. El dashboard Angular invoca correctamente `POST /api/v1/content/generate`; el fallo se producia en backend durante la extraccion de enlaces relevantes desde el HTML de noticias.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/HttpRelevantContentLinkExtractor.java`
- `backend/src/test/java/es/sindicato/intelligence/content/infrastructure/HttpRelevantContentLinkExtractorTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se descartan `href` que no puedan resolverse como URI valida en `HttpRelevantContentLinkExtractor.resolve`.
- No se codifican automaticamente URLs sociales porque no son enlaces oficiales permitidos para el prompt editorial.
- No se modifica Angular ni el contrato REST: la regla de tolerancia pertenece al adaptador backend que inspecciona HTML externo.
- Se incrementa la version backend a `0.0.119-SNAPSHOT` y se registra el arreglo en `CHANGELOG.md`.

# Pruebas o verificaciones

- `mvnw.cmd "-Dtest=HttpRelevantContentLinkExtractorTest" test` OK: 5 tests, 0 fallos, 0 errores.
