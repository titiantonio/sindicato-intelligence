# Estabilizacion WF-04 analisis Gemini

## Fecha

2026-06-26

## Objetivo

Reducir fallos recurrentes en `WF-04_ANALYSIS` donde Gemini devolvia respuestas de analisis degradadas, repetitivas, mezcladas con ingles o sin cierre JSON valido.

## Contexto

- Fase MVP afectada: Fase 8 Analisis IA.
- Documento 31: tarea `16.48 Estabilizacion de analisis IA WF-04`.
- `WF-04` esta migrado a Spring Boot; no se reintroduce logica en n8n.
- La traza mostraba respuestas que empezaban como JSON pero degeneraban en repeticion y no cerraban el objeto.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisPromptBuilder.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisPromptBuilderTest.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/infrastructure/GeminiAnalysisAIProviderTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Mantener el prompt oficial WF-04 como base, reforzandolo con JSON estricto, idioma espanol, brevedad y no repeticion.
- Recortar contenido de noticias por item y limitar el contexto total del prompt para evitar entradas largas o ruidosas.
- Aplicar parametros efectivos conservadores solo en `GeminiAnalysisAIProvider`: temperatura maxima `0.1`, `topP=0.2`, `topK=1`, `candidateCount=1` y minimo `2048` tokens de salida.
- No modificar migraciones ya ejecutadas ni cambiar la arquitectura de WF-04.

## Pruebas y verificacion

Ejecutado desde `backend`:

```powershell
mvn "-Dtest=GenerateAnalysisPromptBuilderTest,GeminiAnalysisAIProviderTest,GenerateAnalysisUseCaseTest" test
```

Resultado:

- 8 tests ejecutados.
- 0 fallos.
- 0 errores.

## Notas operativas

Si `WF04_ANALYSIS` esta configurado en ADMIN con Gemini y un modelo poco estable, el proveedor aplica parametros mas restrictivos para analisis aunque la configuracion general tenga temperatura mayor o limite de salida menor. Para aplicar el cambio en runtime es necesario reiniciar el backend desplegado.
