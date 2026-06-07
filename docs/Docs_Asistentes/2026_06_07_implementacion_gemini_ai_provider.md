# Implementacion Gemini AIProvider

## Fecha

2026-06-07

## Objetivo

Implementar un proveedor IA externo real para clasificacion de noticias usando Gemini/Gemma mediante el puerto `AIProvider`, manteniendo `DeterministicAIProvider` como proveedor por defecto.

## Contexto

La arquitectura del proyecto contempla proveedores IA intercambiables mediante `AIProvider`. El usuario confirmo que en Google AI Studio el identificador canonico del modelo gratuito de pruebas es `models/gemma-4-31b-it`, con soporte para `generateContent`.

## Fase MVP

Documento 30, Fase 6: Clasificacion IA.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/AIProviderException.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/api/ClassificationController.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/AiProviderProperties.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/DeterministicAIProvider.java`.
- `backend/src/main/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProvider.java`.
- `backend/src/main/resources/application.yml`.
- `backend/src/main/resources/application-prod.yml`.
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/AIProviderSelectionTest.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/infrastructure/GeminiAIProviderTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_07_implementacion_gemini_ai_provider.md`.

## Decisiones

- `ClassifyNewsUseCase` no se modifica y sigue dependiendo solo del puerto `AIProvider`.
- `DeterministicAIProvider` queda activo por defecto con `app.ai.provider=deterministic` o si no se configura proveedor.
- `GeminiAIProvider` se activa solo con `app.ai.provider=gemini`.
- No hay fallback silencioso: si Gemini falla, se lanza `AIProviderException`.
- `ClassificationController` traduce `AIProviderException` a `502 Bad Gateway` con mensaje JSON claro.
- La API key no se guarda en base de datos; se lee desde `GEMINI_API_KEY`.
- El modelo por defecto queda como `models/gemma-4-31b-it` y puede cambiarse con `GEMINI_MODEL`.
- Se usa `RestClient`, disponible por `spring-boot-starter-web`, sin añadir dependencias nuevas.
- El proveedor limpia respuestas con fences Markdown y exige JSON valido con enums oficiales.

## Configuracion de uso

Por defecto:

```yaml
app:
  ai:
    provider: deterministic
```

Para usar Gemini/Gemma:

```powershell
$env:AI_PROVIDER="gemini"
$env:GEMINI_API_KEY="tu_api_key"
$env:GEMINI_MODEL="models/gemma-4-31b-it"
```

Propiedades disponibles:

```yaml
app:
  ai:
    provider: ${AI_PROVIDER:deterministic}
    gemini:
      api-key: ${GEMINI_API_KEY:}
      model: ${GEMINI_MODEL:models/gemma-4-31b-it}
      temperature: ${GEMINI_TEMPERATURE:0.2}
      max-output-tokens: ${GEMINI_MAX_OUTPUT_TOKENS:1024}
```

## Documento 31 actualizado

- Se anadio nota posterior en Sprint 5, T5.4, indicando que `GeminiAIProvider` queda implementado como proveedor externo activable por configuracion tecnica.

## Pruebas y verificaciones

- Ejecutado `mvn test -Dtest=GeminiAIProviderTest,AIProviderSelectionTest,ClassificationControllerTest,ClassifyNewsUseCaseTest`.
- Resultado: `Tests run: 11, Failures: 0, Errors: 0, Skipped: 0`.
- Ejecutado `mvn test`.
- Resultado final: `Tests run: 100, Failures: 0, Errors: 0, Skipped: 0`.
- No se realizo llamada real a Gemini en tests; se uso `MockRestServiceServer`.

## Resultado

El backend puede clasificar noticias usando el proveedor determinista por defecto o Gemini/Gemma si se activa por configuracion. Los errores del proveedor externo se exponen de forma explicita y no se ocultan con fallback.
