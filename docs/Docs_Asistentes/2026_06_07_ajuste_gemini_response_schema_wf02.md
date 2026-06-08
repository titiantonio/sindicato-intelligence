# Ajuste Gemini response schema WF-02

## Fecha

2026-06-07

## Objetivo

Corregir el comportamiento observado en `WF-02-Classify-News`, donde Gemini/Gemma respondia reformulando el prompt en texto en lugar de devolver el objeto JSON de clasificacion.

## Contexto

- Fase MVP: Fase 6, clasificacion IA.
- Documento 30: clasificacion IA de noticias capturadas.
- Documento 31: T5.4 integracion `AIProvider`, T5.6 workflow n8n.
- Logs observados: diez peticiones simultaneas iniciadas y fallos `Gemini response does not contain a JSON object`.
- Fragmentos de respuesta: el modelo devolvia un resumen del rol, tarea y reglas en ingles, no el JSON esperado.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProviderTest.java`
- `n8n/workflows/wf_02_classify_news.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- Separar el prompt de sistema usando `systemInstruction` en la peticion a Gemini.
- Enviar el prompt de usuario solo en `contents` para evitar que el modelo trate todo el texto como contenido a resumir.
- Añadir `responseSchema` a `generationConfig` para forzar las claves y enums esperados por el contrato de clasificacion.
- Reducir temporalmente `WF-02-Classify-News` a 1 noticia por ejecucion cada 5 minutos, evitando rafagas paralelas mientras se estabiliza la salida JSON del proveedor.
- Mantener sin fallback silencioso al proveedor determinista.

## Verificaciones

- `mvn "-Dtest=GeminiAIProviderTest,AIProviderSelectionTest" test`: 6 tests, 0 fallos, 0 errores.
- Validado JSON de `n8n/workflows/wf_02_classify_news.json` con `ConvertFrom-Json`.
- `mvn test`: 100 tests, 0 fallos, 0 errores.

## Nota operativa

Reiniciar el backend y reimportar/actualizar `WF-02-Classify-News` en n8n. Si el modelo configurado no soporta `responseSchema`, el backend devolvera un error HTTP claro desde Gemini. En ese caso habra que cambiar `GEMINI_MODEL` a un modelo Gemini que soporte salida estructurada.
