package es.sindicato.intelligence.content.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.ai.application.AiErrorSanitizer;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.content.application.ContentAIProvider;
import es.sindicato.intelligence.content.application.ContentAIProviderException;
import es.sindicato.intelligence.content.application.ContentAIRequest;
import es.sindicato.intelligence.content.application.ContentAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GeminiContentAIProvider implements ContentAIProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiContentAIProvider.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final int MAX_CONTENT_ATTEMPTS = 2;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final double temperature;
    private final int maxOutputTokens;

    public GeminiContentAIProvider(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${app.ai.gemini.api-key:}") String apiKey,
            @Value("${app.ai.gemini.model:models/gemma-4-31b-it}") String model,
            @Value("${app.ai.gemini.temperature:0.2}") double temperature,
            @Value("${app.ai.gemini.max-output-tokens:1024}") int maxOutputTokens
    ) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
        this.maxOutputTokens = maxOutputTokens;
    }

    @Override
    public ContentAIResponse generate(ContentAIRequest request) {
        String resolvedApiKey = requireText(apiKey, "Gemini API key is required when app.ai.provider=gemini");
        String resolvedModel = normalizeModel(requireText(model, "Gemini model is required when app.ai.provider=gemini"));
        return generate(request, resolvedApiKey, resolvedModel, temperature, maxOutputTokens);
    }

    public ContentAIResponse generate(ContentAIRequest request, AiWorkflowRuntimeSettings settings) {
        return generate(
                request,
                requireText(settings.apiKey(), "Gemini API key is required"),
                normalizeModel(requireText(settings.modelName(), "Gemini model is required")),
                settings.temperature().doubleValue(),
                settings.maxOutputTokens()
        );
    }

    private ContentAIResponse generate(ContentAIRequest request, String resolvedApiKey, String resolvedModel, double resolvedTemperature, int resolvedMaxOutputTokens) {
        ContentAIProviderException lastException = null;
        for (int attempt = 1; attempt <= MAX_CONTENT_ATTEMPTS; attempt++) {
            try {
                JsonNode response = callGemini(request, resolvedApiKey, resolvedModel, resolvedTemperature, resolvedMaxOutputTokens, attempt > 1);
                String responseText = extractResponseText(response);
                return parseContentResponse(responseText);
            } catch (ContentAIProviderException exception) {
                lastException = exception;
                if (attempt >= MAX_CONTENT_ATTEMPTS || !isRetryable(exception)) {
                    throw exception;
                }

                log.warn("Gemini content response invalid, retrying with reduced context: attempt={}, maxAttempts={}, reason={}", attempt, MAX_CONTENT_ATTEMPTS, exception.getMessage());
            }
        }

        throw lastException;
    }

    @Override
    public String providerName() {
        return "gemini";
    }

    @Override
    public String modelName() {
        return normalizeModel(model);
    }

    private JsonNode callGemini(ContentAIRequest request, String resolvedApiKey, String resolvedModel, double resolvedTemperature, int resolvedMaxOutputTokens, boolean reducedContext) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", request.systemPrompt()))
                ),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildUserPrompt(request, reducedContext)))
                )),
                "generationConfig", Map.of(
                        "temperature", resolvedTemperature,
                        "maxOutputTokens", resolvedMaxOutputTokens,
                        "responseMimeType", "application/json",
                        "responseSchema", contentResponseSchema()
                )
        );

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + resolvedModel + ":generateContent")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", resolvedApiKey)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new ContentAIProviderException(
                    AiErrorSanitizer.providerHttpError("content", exception),
                    exception
            );
        } catch (RestClientException exception) {
            throw new ContentAIProviderException("Gemini content request failed: " + exception.getMessage(), exception);
        }
    }

    private String buildUserPrompt(ContentAIRequest request, boolean reducedContext) {
        String prompt = reducedContext ? reducedUserPrompt(request) : request.userPrompt();
        return prompt + """

                Reglas obligatorias de respuesta:
                - Tu salida debe ser el objeto JSON final de contenido, no un resumen de estas instrucciones.
                - Empieza directamente por { y termina directamente por }.
                - Devuelve solo JSON valido, sin markdown.
                - title y message deben ser strings.
                - hashtags debe ser un array de strings, cada item empezando por #.
                """;
    }

    private String reducedUserPrompt(ContentAIRequest request) {
        return """
                EVENTO:
                id: %s
                titulo: %s
                categoria: %s
                importancia: %s

                ANALISIS REDUCIDO:
                resumen ejecutivo: %s
                resumen sindical: %s
                colectivos afectados: %s
                seguimiento recomendado: %s

                PARAMETROS:
                canal: %s
                tono: %s
                tipo contenido: %s
                longitud: %s

                ENLACES RELEVANTES PERMITIDOS:
                %s

                Genera un objeto JSON con exactamente esta estructura:
                {
                  "title": "",
                  "message": "",
                  "hashtags": []
                }
                """.formatted(
                request.event().getId(),
                safe(request.event().getTitle()),
                request.event().getCategory(),
                request.event().getImportance(),
                safe(request.analysis().getExecutiveSummary()),
                safe(request.analysis().getUnionSummary()),
                request.analysis().getAffectedGroups(),
                request.analysis().getRecommendedMonitoring(),
                request.channel(),
                request.tone(),
                request.contentType(),
                request.length(),
                relevantLinks(request)
        );
    }

    private String relevantLinks(ContentAIRequest request) {
        if (request.relevantLinks() == null || request.relevantLinks().isEmpty()) {
            return "Sin enlaces relevantes permitidos.";
        }
        StringBuilder builder = new StringBuilder();
        for (var link : request.relevantLinks().stream().limit(3).toList()) {
            builder.append("- ").append(safe(link.label())).append(": ").append(safe(link.url())).append('\n');
        }
        return builder.toString().trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Map<String, Object> contentResponseSchema() {
        return Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "title", Map.of("type", "STRING"),
                        "message", Map.of("type", "STRING"),
                        "hashtags", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"))
                ),
                "required", List.of("title", "message", "hashtags"),
                "propertyOrdering", List.of("title", "message", "hashtags")
        );
    }

    private String extractResponseText(JsonNode response) {
        if (response == null) {
            throw new ContentAIProviderException("Gemini response is empty");
        }

        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (!textNode.isTextual() || textNode.asText().isBlank()) {
            log.warn("Gemini content response does not contain text. diagnostics='{}'", geminiDiagnostics(response));
            throw new ContentAIProviderException("Gemini response does not contain candidates[0].content.parts[0].text");
        }

        return textNode.asText();
    }

    private ContentAIResponse parseContentResponse(String responseText) {
        String json = extractJsonObject(responseText);

        try {
            JsonNode root = objectMapper.readTree(json);
            return new ContentAIResponse(
                    requiredText(root, "title"),
                    requiredText(root, "message"),
                    stringList(root, "hashtags")
            );
        } catch (JsonProcessingException exception) {
            throw new ContentAIProviderException("Gemini response is not valid content JSON", exception);
        }
    }

    private String extractJsonObject(String value) {
        String text = value == null ? "" : value.trim();

        if (text.startsWith("```")) {
            int firstLineEnd = text.indexOf('\n');
            int lastFence = text.lastIndexOf("```");
            if (firstLineEnd >= 0 && lastFence > firstLineEnd) {
                text = text.substring(firstLineEnd + 1, lastFence).trim();
            }
        }

        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');

        if (firstBrace < 0 || lastBrace <= firstBrace) {
            log.warn("Gemini content response does not contain JSON object. responseSnippet='{}'", abbreviate(text));
            throw new ContentAIProviderException("Gemini response does not contain a JSON object");
        }

        return text.substring(firstBrace, lastBrace + 1);
    }

    private boolean isRetryable(ContentAIProviderException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }

        return message.startsWith("Gemini response is empty")
                || message.startsWith("Gemini response does not contain")
                || message.startsWith("Gemini response is not valid content JSON")
                || message.startsWith("Gemini response field '");
    }

    private String geminiDiagnostics(JsonNode response) {
        if (response == null) {
            return "empty response";
        }

        JsonNode finishReason = response.at("/candidates/0/finishReason");
        JsonNode blockReason = response.at("/promptFeedback/blockReason");
        JsonNode safetyRatings = response.at("/candidates/0/safetyRatings");

        return "finishReason=" + textOrMissing(finishReason)
                + ", blockReason=" + textOrMissing(blockReason)
                + ", safetyRatings=" + abbreviate(safetyRatings.isMissingNode() ? "" : safetyRatings.toString());
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual() || node.asText().isBlank()) {
            throw new ContentAIProviderException("Gemini response field '" + fieldName + "' must be textual");
        }

        return node.asText();
    }

    private List<String> stringList(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) {
            return List.of();
        }

        if (!node.isArray()) {
            throw new ContentAIProviderException("Gemini response field '" + fieldName + "' must be an array");
        }

        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            if (item.isTextual() && !item.asText().isBlank()) {
                values.add(item.asText());
            }
        }

        return values;
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ContentAIProviderException(message);
        }

        return value.trim();
    }

    private String normalizeModel(String model) {
        return model.startsWith("/") ? model.substring(1) : model;
    }

    private String textOrMissing(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "missing";
        }

        return node.asText();
    }

    private String abbreviate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmed = value.replaceAll("\\s+", " ").trim();
        if (trimmed.length() <= 500) {
            return trimmed;
        }

        return trimmed.substring(0, 497) + "...";
    }
}
