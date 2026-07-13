package es.sindicato.intelligence.classification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.ai.application.AiErrorSanitizer;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.classification.application.AIProvider;
import es.sindicato.intelligence.classification.application.AIProviderException;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class GeminiAIProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAIProvider.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final int MAX_CLASSIFICATION_ATTEMPTS = 2;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AiProviderProperties properties;

    public GeminiAIProvider(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            AiProviderProperties properties
    ) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public ClassificationAIResponse classify(ClassificationAIRequest request) {
        AiProviderProperties.Gemini gemini = properties.getGemini();
        String apiKey = requireText(gemini.getApiKey(), "Gemini API key is required when app.ai.provider=gemini");
        String model = normalizeModel(requireText(gemini.getModel(), "Gemini model is required when app.ai.provider=gemini"));
        return classify(request, apiKey, model, gemini.getTemperature(), gemini.getMaxOutputTokens());
    }

    public ClassificationAIResponse classify(ClassificationAIRequest request, AiWorkflowRuntimeSettings settings) {
        return classify(
                request,
                requireText(settings.apiKey(), "Gemini API key is required"),
                normalizeModel(requireText(settings.modelName(), "Gemini model is required")),
                settings.temperature().doubleValue(),
                settings.maxOutputTokens()
        );
    }

    private ClassificationAIResponse classify(ClassificationAIRequest request, String apiKey, String model, double temperature, int maxOutputTokens) {
        AIProviderException lastException = null;
        for (int attempt = 1; attempt <= MAX_CLASSIFICATION_ATTEMPTS; attempt++) {
            try {
                JsonNode response = callGemini(request, apiKey, model, temperature, maxOutputTokens);
                String responseText = extractResponseText(response);
                return parseClassificationResponse(responseText);
            } catch (AIProviderException exception) {
                lastException = exception;
                if (attempt >= MAX_CLASSIFICATION_ATTEMPTS || !isRetryable(exception)) {
                    throw exception;
                }

                log.warn("Gemini classification response invalid, retrying: attempt={}, maxAttempts={}, reason={}", attempt, MAX_CLASSIFICATION_ATTEMPTS, exception.getMessage());
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
        return normalizeModel(properties.getGemini().getModel());
    }

    private JsonNode callGemini(
            ClassificationAIRequest request,
            String apiKey,
            String model,
            double temperature,
            int maxOutputTokens
    ) {
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
                        "responseSchema", classificationResponseSchema()
                )
        );

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + model + ":generateContent")
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", apiKey)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new AIProviderException(
                    AiErrorSanitizer.providerHttpError("classification", exception),
                    exception
            );
        } catch (RestClientException exception) {
            throw new AIProviderException("Gemini classification request failed: " + exception.getMessage(), exception);
        }
    }

    private String buildUserPrompt(ClassificationAIRequest request) {
        return request.userPrompt() + """

                Reglas obligatorias de respuesta:
                - Tu salida debe ser el objeto JSON final de clasificacion, no un resumen de estas instrucciones.
                - Empieza directamente por { y termina directamente por }.
                - Devuelve solo JSON valido, sin markdown.
                - Usa una categoria exacta de esta lista: OPOSICIONES, INTERINOS, SIPRI, PLANTILLAS, RETRIBUCIONES, FORMACION, INSPECCION, LEGISLACION, CURRICULO, UNIVERSIDAD, FP, DIGITALIZACION, INCLUSION, INFRAESTRUCTURAS, CONFLICTO_LABORAL, SINDICAL, OTROS.
                - Usa impact exacto de esta lista: LOW, MEDIUM, HIGH, CRITICAL.
                - Usa urgency exacto de esta lista: LOW, MEDIUM, HIGH.
                - relevance debe ser un numero entre 0 y 100.
                - Para noticias clasificables puedes devolver keywords y entities como arrays de strings, summary como texto breve y classificationReason como una frase breve de justificacion.
                - Para category OTROS con subcategory FUERA_DE_AMBITO o INFORMACION_INSUFICIENTE no devuelvas keywords, entities ni summary.
                """;
    }

    private Map<String, Object> classificationResponseSchema() {
        return Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "category", Map.of(
                                "type", "STRING",
                                "enum", List.of("OPOSICIONES", "INTERINOS", "SIPRI", "PLANTILLAS", "RETRIBUCIONES", "FORMACION", "INSPECCION", "LEGISLACION", "CURRICULO", "UNIVERSIDAD", "FP", "DIGITALIZACION", "INCLUSION", "INFRAESTRUCTURAS", "CONFLICTO_LABORAL", "SINDICAL", "OTROS")
                        ),
                        "subcategory", Map.of("type", "STRING"),
                        "relevance", Map.of("type", "NUMBER"),
                        "impact", Map.of(
                                "type", "STRING",
                                "enum", List.of("LOW", "MEDIUM", "HIGH", "CRITICAL")
                        ),
                        "urgency", Map.of(
                                "type", "STRING",
                                "enum", List.of("LOW", "MEDIUM", "HIGH")
                        ),
                        "keywords", Map.of(
                                "type", "ARRAY",
                                "items", Map.of("type", "STRING")
                        ),
                        "entities", Map.of(
                                "type", "ARRAY",
                                "items", Map.of("type", "STRING")
                        ),
                        "summary", Map.of("type", "STRING"),
                        "classificationReason", Map.of("type", "STRING")
                ),
                "required", List.of("category", "subcategory", "relevance", "impact", "urgency"),
                "propertyOrdering", List.of("category", "subcategory", "relevance", "impact", "urgency", "keywords", "entities", "summary", "classificationReason")
        );
    }

    private String extractResponseText(JsonNode response) {
        if (response == null) {
            throw new AIProviderException("Gemini response is empty");
        }

        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (!textNode.isTextual() || textNode.asText().isBlank()) {
            log.warn("Gemini response does not contain text. diagnostics='{}'", geminiDiagnostics(response));
            throw new AIProviderException("Gemini response does not contain candidates[0].content.parts[0].text");
        }

        return textNode.asText();
    }

    private ClassificationAIResponse parseClassificationResponse(String responseText) {
        String json = extractJsonObject(responseText);

        try {
            JsonNode root = objectMapper.readTree(json);
            BigDecimal relevance = requiredBigDecimal(root, "relevance");
            validateRelevance(relevance);

            return new ClassificationAIResponse(
                    requiredEnum(ClassificationCategory.class, root, "category"),
                    optionalText(root, "subcategory"),
                    relevance,
                    requiredEnum(ImpactLevel.class, root, "impact"),
                    requiredEnum(UrgencyLevel.class, root, "urgency"),
                    stringList(root, "keywords"),
                    stringList(root, "entities"),
                    optionalText(root, "summary"),
                    optionalText(root, "classificationReason")
            );
        } catch (JsonProcessingException exception) {
            throw new AIProviderException("Gemini response is not valid classification JSON", exception);
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
            log.warn("Gemini response does not contain JSON object. responseSnippet='{}'", abbreviate(text));
            throw new AIProviderException("Gemini response does not contain a JSON object");
        }

        return text.substring(firstBrace, lastBrace + 1);
    }

    private boolean isRetryable(AIProviderException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }

        return message.startsWith("Gemini response is empty")
                || message.startsWith("Gemini response does not contain")
                || message.startsWith("Gemini response is not valid classification JSON")
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

    private String textOrMissing(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return "missing";
        }

        return node.asText();
    }

    private String optionalText(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) {
            return "";
        }

        return node.asText("");
    }

    private BigDecimal requiredBigDecimal(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isNumber()) {
            throw new AIProviderException("Gemini response field '" + fieldName + "' must be numeric");
        }

        return node.decimalValue();
    }

    private void validateRelevance(BigDecimal relevance) {
        if (relevance.compareTo(BigDecimal.ZERO) < 0 || relevance.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new AIProviderException("Gemini response field 'relevance' must be between 0 and 100");
        }
    }

    private <T extends Enum<T>> T requiredEnum(Class<T> enumType, JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || !node.isTextual()) {
            throw new AIProviderException("Gemini response field '" + fieldName + "' must be textual");
        }

        String value = normalizeEnumValue(node.asText());
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException exception) {
            throw new AIProviderException("Gemini response field '" + fieldName + "' has unsupported value: " + node.asText(), exception);
        }
    }

    private String normalizeEnumValue(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }

    private List<String> stringList(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) {
            return List.of();
        }

        if (!node.isArray()) {
            throw new AIProviderException("Gemini response field '" + fieldName + "' must be an array");
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
            throw new AIProviderException(message);
        }

        return value.trim();
    }

    private String normalizeModel(String model) {
        return model.startsWith("/") ? model.substring(1) : model;
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
