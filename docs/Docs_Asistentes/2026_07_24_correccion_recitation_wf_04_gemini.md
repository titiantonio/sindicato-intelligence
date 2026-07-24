# Correccion RECITATION WF-04 Gemini

Fecha: 2026-07-24

## Objetivo

Diagnosticar y corregir el fallo de `WF04_ANALYSIS` cuando Gemini devuelve una respuesta sin texto por `finishReason=RECITATION`.

## Contexto

Intervencion de mantenimiento correctivo posterior al Sprint 12, correspondiente a la Fase 12 del Documento 30: automatizaciones internas, IA y observabilidad.

El log indica que WF-04 ya se ejecuta, pero el proveedor Gemini corta la generacion por recitacion y no devuelve `candidates[0].content.parts[0].text`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProviderTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Se mantiene el prompt oficial y el contrato JSON de WF-04.
- Si Gemini falla por `RECITATION`, el segundo intento elimina las lineas `contenido:` del contexto de noticias.
- El reintento conserva datos suficientes para el analisis: evento, titulo, fuente, URL, resumen y fecha.
- No se mueve logica de negocio a n8n ni se cambian endpoints, dominio o migraciones.

## Pruebas y verificaciones

- `mvn "-Dtest=GeminiAnalysisAIProviderTest,ProcessPendingEventAnalysisUseCaseTest" test`: OK, 9 tests.
