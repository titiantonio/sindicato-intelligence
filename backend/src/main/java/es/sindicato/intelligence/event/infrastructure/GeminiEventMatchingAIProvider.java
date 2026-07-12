package es.sindicato.intelligence.event.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.ai.application.AiErrorSanitizer;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.event.application.EventMatchingAIProvider;
import es.sindicato.intelligence.event.application.EventMatchingAIRequest;
import es.sindicato.intelligence.event.application.EventMatchingAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class GeminiEventMatchingAIProvider implements EventMatchingAIProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiEventMatchingAIProvider.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final int MAX_ATTEMPTS = 2;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiEventMatchingAIProvider(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
        this.objectMapper = objectMapper;
    }

    public EventMatchingAIResponse match(EventMatchingAIRequest request, AiWorkflowRuntimeSettings settings) {
        String apiKey = requireText(settings.apiKey(), "Gemini API key is required");
        String model = normalizeModel(requireText(settings.modelName(), "Gemini model is required"));
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                JsonNode response = callGemini(request, apiKey, model, settings.temperature().doubleValue(), settings.maxOutputTokens());
                return parseResponse(extractResponseText(response));
            } catch (RuntimeException exception) {
                lastException = exception;
                if (attempt >= MAX_ATTEMPTS || !isRetryable(exception)) {
                    throw exception;
                }
                log.warn("Gemini event matching response invalid, retrying: attempt={}, maxAttempts={}, reason={}", attempt, MAX_ATTEMPTS, exception.getMessage());
            }
        }
        throw lastException;
    }

    @Override
    public EventMatchingAIResponse match(EventMatchingAIRequest request) {
        throw new IllegalStateException("Gemini event matching requires workflow runtime settings");
    }

    @Override
    public String providerName() {
        return "gemini";
    }

    @Override
    public String modelName() {
        return "gemini";
    }

    private JsonNode callGemini(EventMatchingAIRequest request, String apiKey, String model, double temperature, int maxOutputTokens) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", request.systemPrompt()))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildUserPrompt(request)))
                )),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxOutputTokens,
                        "responseMimeType", "application/json",
                        "responseSchema", responseSchema()
                )
        );
        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/" + model + ":generateContent").build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", apiKey)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(AiErrorSanitizer.providerHttpError("event matching", exception), exception);
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Gemini event matching request failed", exception);
        }
    }

    private String buildUserPrompt(EventMatchingAIRequest request) {
        return request.userPrompt() + """

                Reglas obligatorias de respuesta:
                - Devuelve solo JSON valido, sin markdown.
                - match debe ser boolean.
                - eventId debe ser null si no hay coincidencia.
                - confidence debe ser un entero entre 0 y 100.
                - reason debe explicar brevemente la decision.
                """;
    }

    private Map<String, Object> responseSchema() {
        return Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "match", Map.of("type", "BOOLEAN"),
                        "eventId", Map.of("type", "INTEGER", "nullable", true),
                        "confidence", Map.of("type", "INTEGER"),
                        "reason", Map.of("type", "STRING")
                ),
                "required", List.of("match", "eventId", "confidence", "reason"),
                "propertyOrdering", List.of("match", "eventId", "confidence", "reason")
        );
    }

    private String extractResponseText(JsonNode response) {
        if (response == null) {
            throw new IllegalArgumentException("Gemini response is empty");
        }
        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (!textNode.isTextual() || textNode.asText().isBlank()) {
            throw new IllegalArgumentException("Gemini response does not contain candidates[0].content.parts[0].text");
        }
        return textNode.asText();
    }

    private EventMatchingAIResponse parseResponse(String responseText) {
        try {
            JsonNode root = objectMapper.readTree(extractJsonObject(responseText));
            validateRequiredFields(root);
            int confidence = root.path("confidence").asInt(-1);
            if (confidence < 0 || confidence > 100) {
                throw new IllegalArgumentException("Gemini event matching confidence is outside 0-100");
            }
            return new EventMatchingAIResponse(
                    root.path("match").asBoolean(false),
                    root.path("eventId").isNull() || root.path("eventId").isMissingNode() ? null : root.path("eventId").asLong(),
                    confidence,
                    root.path("reason").asText("")
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Gemini response is not valid event matching JSON", exception);
        }
    }

    private void validateRequiredFields(JsonNode root) {
        if (!root.path("match").isBoolean()) {
            throw new IllegalArgumentException("Gemini event matching JSON does not contain boolean match");
        }
        if (!root.path("confidence").isInt()) {
            throw new IllegalArgumentException("Gemini event matching JSON does not contain integer confidence");
        }
        if (!root.path("reason").isTextual()) {
            throw new IllegalArgumentException("Gemini event matching JSON does not contain textual reason");
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
            throw new IllegalArgumentException("Gemini response does not contain a JSON object");
        }
        return text.substring(firstBrace, lastBrace + 1);
    }

    private boolean isRetryable(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && message.startsWith("Gemini response");
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalizeModel(String model) {
        return model.startsWith("/") ? model.substring(1) : model;
    }
}
