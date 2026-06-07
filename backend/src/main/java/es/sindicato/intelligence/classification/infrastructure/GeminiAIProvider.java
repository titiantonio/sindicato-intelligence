package es.sindicato.intelligence.classification.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.classification.application.AIProvider;
import es.sindicato.intelligence.classification.application.AIProviderException;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
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
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini")
public class GeminiAIProvider implements AIProvider {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

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

        JsonNode response = callGemini(request, gemini, apiKey, model);
        String responseText = extractResponseText(response);
        return parseClassificationResponse(responseText);
    }

    private JsonNode callGemini(
            ClassificationAIRequest request,
            AiProviderProperties.Gemini gemini,
            String apiKey,
            String model
    ) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildPrompt(request)))
                )),
                "generationConfig", Map.of(
                        "temperature", gemini.getTemperature(),
                        "maxOutputTokens", gemini.getMaxOutputTokens(),
                        "responseMimeType", "application/json"
                )
        );

        try {
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            throw new AIProviderException(
                    "Gemini classification request failed with HTTP " + exception.getStatusCode().value()
                            + ": " + exception.getResponseBodyAsString(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new AIProviderException("Gemini classification request failed: " + exception.getMessage(), exception);
        }
    }

    private String buildPrompt(ClassificationAIRequest request) {
        return request.systemPrompt() + "\n\n" + request.userPrompt() + """

                Reglas obligatorias de respuesta:
                - Devuelve solo JSON valido, sin markdown.
                - Usa una categoria exacta de esta lista: OPOSICIONES, INTERINOS, SIPRI, PLANTILLAS, RETRIBUCIONES, FORMACION, INSPECCION, LEGISLACION, CURRICULO, UNIVERSIDAD, FP, DIGITALIZACION, INCLUSION, INFRAESTRUCTURAS, CONFLICTO_LABORAL, SINDICAL, OTROS.
                - Usa impact exacto de esta lista: LOW, MEDIUM, HIGH, CRITICAL.
                - Usa urgency exacto de esta lista: LOW, MEDIUM, HIGH.
                - relevance debe ser un numero entre 0 y 100.
                - keywords y entities deben ser arrays de strings.
                """;
    }

    private String extractResponseText(JsonNode response) {
        if (response == null) {
            throw new AIProviderException("Gemini response is empty");
        }

        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (!textNode.isTextual() || textNode.asText().isBlank()) {
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
                    optionalText(root, "summary")
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
            throw new AIProviderException("Gemini response does not contain a JSON object");
        }

        return text.substring(firstBrace, lastBrace + 1);
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
}
