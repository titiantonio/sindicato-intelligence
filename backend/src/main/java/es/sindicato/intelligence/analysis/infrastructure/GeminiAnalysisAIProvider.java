package es.sindicato.intelligence.analysis.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.analysis.application.AnalysisAIProvider;
import es.sindicato.intelligence.analysis.application.AnalysisAIProviderException;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini")
public class GeminiAnalysisAIProvider implements AnalysisAIProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAnalysisAIProvider.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final int MAX_ANALYSIS_ATTEMPTS = 2;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final double temperature;
    private final int maxOutputTokens;

    public GeminiAnalysisAIProvider(
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
    public AnalysisAIResponse generate(AnalysisAIRequest request) {
        String resolvedApiKey = requireText(apiKey, "Gemini API key is required when app.ai.provider=gemini");
        String resolvedModel = normalizeModel(requireText(model, "Gemini model is required when app.ai.provider=gemini"));

        AnalysisAIProviderException lastException = null;
        for (int attempt = 1; attempt <= MAX_ANALYSIS_ATTEMPTS; attempt++) {
            try {
                JsonNode response = callGemini(request, resolvedApiKey, resolvedModel);
                String responseText = extractResponseText(response);
                return parseAnalysisResponse(responseText, resolvedModel);
            } catch (AnalysisAIProviderException exception) {
                lastException = exception;
                if (attempt >= MAX_ANALYSIS_ATTEMPTS || !isRetryable(exception)) {
                    throw exception;
                }

                log.warn("Gemini analysis response invalid, retrying: attempt={}, maxAttempts={}, reason={}", attempt, MAX_ANALYSIS_ATTEMPTS, exception.getMessage());
            }
        }

        throw lastException;
    }

    private JsonNode callGemini(AnalysisAIRequest request, String resolvedApiKey, String resolvedModel) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", request.systemPrompt()))
                ),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildUserPrompt(request)))
                )),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxOutputTokens,
                        "responseMimeType", "application/json",
                        "responseSchema", analysisResponseSchema()
                )
        );

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + resolvedModel + ":generateContent")
                            .queryParam("key", resolvedApiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new AnalysisAIProviderException(
                    "Gemini analysis request failed with HTTP " + exception.getStatusCode().value()
                            + ": " + exception.getResponseBodyAsString(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new AnalysisAIProviderException("Gemini analysis request failed: " + exception.getMessage(), exception);
        }
    }

    private String buildUserPrompt(AnalysisAIRequest request) {
        return request.userPrompt() + """

                Reglas obligatorias de respuesta:
                - Tu salida debe ser el objeto JSON final de analisis, no un resumen de estas instrucciones.
                - Empieza directamente por { y termina directamente por }.
                - Devuelve solo JSON valido, sin markdown.
                - executiveSummary y unionSummary deben ser strings.
                - keyPoints, risks, opportunities, affectedGroups y recommendedMonitoring deben ser arrays de strings.
                """;
    }

    private Map<String, Object> analysisResponseSchema() {
        Map<String, Object> stringArray = Map.of("type", "ARRAY", "items", Map.of("type", "STRING"));

        return Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "executiveSummary", Map.of("type", "STRING"),
                        "unionSummary", Map.of("type", "STRING"),
                        "keyPoints", stringArray,
                        "risks", stringArray,
                        "opportunities", stringArray,
                        "affectedGroups", stringArray,
                        "recommendedMonitoring", stringArray
                ),
                "required", List.of("executiveSummary", "unionSummary", "keyPoints", "risks", "opportunities", "affectedGroups", "recommendedMonitoring"),
                "propertyOrdering", List.of("executiveSummary", "unionSummary", "keyPoints", "risks", "opportunities", "affectedGroups", "recommendedMonitoring")
        );
    }

    private String extractResponseText(JsonNode response) {
        if (response == null) {
            throw new AnalysisAIProviderException("Gemini response is empty");
        }

        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (!textNode.isTextual() || textNode.asText().isBlank()) {
            log.warn("Gemini analysis response does not contain text. diagnostics='{}'", geminiDiagnostics(response));
            throw new AnalysisAIProviderException("Gemini response does not contain candidates[0].content.parts[0].text");
        }

        return textNode.asText();
    }

    private AnalysisAIResponse parseAnalysisResponse(String responseText, String resolvedModel) {
        String json = extractJsonObject(responseText);

        try {
            JsonNode root = objectMapper.readTree(json);
            return new AnalysisAIResponse(
                    requiredText(root, "executiveSummary"),
                    requiredText(root, "unionSummary"),
                    stringList(root, "keyPoints"),
                    stringList(root, "risks"),
                    stringList(root, "opportunities"),
                    resolvedModel
            );
        } catch (JsonProcessingException exception) {
            throw new AnalysisAIProviderException("Gemini response is not valid analysis JSON", exception);
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
            log.warn("Gemini analysis response does not contain JSON object. responseSnippet='{}'", abbreviate(text));
            throw new AnalysisAIProviderException("Gemini response does not contain a JSON object");
        }

        return text.substring(firstBrace, lastBrace + 1);
    }

    private boolean isRetryable(AnalysisAIProviderException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }

        return message.startsWith("Gemini response is empty")
                || message.startsWith("Gemini response does not contain")
                || message.startsWith("Gemini response is not valid analysis JSON")
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
            throw new AnalysisAIProviderException("Gemini response field '" + fieldName + "' must be textual");
        }

        return node.asText();
    }

    private List<String> stringList(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) {
            return List.of();
        }

        if (!node.isArray()) {
            throw new AnalysisAIProviderException("Gemini response field '" + fieldName + "' must be an array");
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
            throw new AnalysisAIProviderException(message);
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
