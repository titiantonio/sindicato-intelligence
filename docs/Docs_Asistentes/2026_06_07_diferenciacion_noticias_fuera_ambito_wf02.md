# Diferenciacion noticias fuera de ambito WF-02

## Fecha

2026-06-07

## Objetivo

Diferenciar claramente las noticias que no pertenecen al ambito de la plataforma frente a las noticias con informacion insuficiente durante la clasificacion IA de `WF-02-Classify-News`.

## Contexto

- Fase MVP: Fase 6, clasificacion IA.
- Documento 30: clasificacion de noticias capturadas mediante IA.
- Documento 31: T5.5, prompt WF-02.
- La mayoria de noticias capturadas por fuentes generalistas pueden no tratar sobre educacion, profesorado, sindicatos docentes o condiciones laborales docentes.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilder.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsPromptBuilderTest.java`
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- No crear una categoria nueva fuera de la taxonomia oficial.
- Usar `category` `OTROS` y `subcategory` `FUERA_DE_AMBITO` para noticias que no tratan sobre educacion, profesorado, sindicatos docentes, normativa educativa, empleo docente, centros educativos, Junta de Andalucia, universidad, FP o condiciones laborales docentes.
- Usar `category` `OTROS` y `subcategory` `INFORMACION_INSUFICIENTE` cuando la noticia podria estar relacionada, pero titulo, resumen y contenido no aportan datos suficientes para decidirlo.
- Mantener `relevance` `0`, `impact` `LOW` y `urgency` `LOW` en ambos casos de descarte.

## Verificaciones

- `mvn "-Dtest=ClassifyNewsPromptBuilderTest,GeminiAIProviderTest" test`: 6 tests, 0 fallos, 0 errores.
- `mvn test`: 100 tests, 0 fallos, 0 errores.

## Nota operativa

Para que n8n reciba esta clasificacion diferenciada debe reiniciarse el backend Spring Boot antes de volver a ejecutar `WF-02-Classify-News`.
