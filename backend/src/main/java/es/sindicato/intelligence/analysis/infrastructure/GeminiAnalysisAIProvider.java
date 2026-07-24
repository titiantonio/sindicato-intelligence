package es.sindicato.intelligence.analysis.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.ai.application.AiErrorSanitizer;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.analysis.application.AnalysisAIProvider;
import es.sindicato.intelligence.analysis.application.AnalysisAIProviderException;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GeminiAnalysisAIProvider implements AnalysisAIProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAnalysisAIProvider.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final int MAX_ANALYSIS_ATTEMPTS = 2;
    private static final double MAX_EFFECTIVE_ANALYSIS_TEMPERATURE = 0.1;
    private static final int MIN_EFFECTIVE_ANALYSIS_OUTPUT_TOKENS = 2_048;
    private static final String RECITATION_FINISH_REASON = "finishReason=RECITATION";

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
        return generate(request, resolvedApiKey, resolvedModel, temperature, maxOutputTokens);
    }

    public AnalysisAIResponse generate(AnalysisAIRequest request, AiWorkflowRuntimeSettings settings) {
        return generate(
                request,
                requireText(settings.apiKey(), "Gemini API key is required"),
                normalizeModel(requireText(settings.modelName(), "Gemini model is required")),
                settings.temperature().doubleValue(),
                settings.maxOutputTokens()
        );
    }

    private AnalysisAIResponse generate(AnalysisAIRequest request, String resolvedApiKey, String resolvedModel, double resolvedTemperature, int resolvedMaxOutputTokens) {
        AnalysisAIProviderException lastException = null;
        AnalysisAIRequest currentRequest = request;
        for (int attempt = 1; attempt <= MAX_ANALYSIS_ATTEMPTS; attempt++) {
            try {
                JsonNode response = callGemini(
                        currentRequest,
                        resolvedApiKey,
                        resolvedModel,
                        effectiveTemperature(resolvedTemperature),
                        effectiveMaxOutputTokens(resolvedMaxOutputTokens)
                );
                String responseText = extractResponseText(response);
                return parseAnalysisResponse(responseText, resolvedModel);
            } catch (AnalysisAIProviderException exception) {
                lastException = exception;
                if (attempt >= MAX_ANALYSIS_ATTEMPTS && isRecitationFallbackRequest(currentRequest)) {
                    log.warn("Gemini analysis fallback used after RECITATION retry failed: reason={}", exception.getMessage());
                    return conservativeRecitationFallback(currentRequest, resolvedModel);
                }
                if (attempt >= MAX_ANALYSIS_ATTEMPTS || !isRetryable(exception)) {
                    throw exception;
                }

                if (isRecitationFailure(exception)) {
                    currentRequest = recitationSafeRequest(currentRequest);
                }
                log.warn("Gemini analysis response invalid, retrying: attempt={}, maxAttempts={}, reason={}", attempt, MAX_ANALYSIS_ATTEMPTS, exception.getMessage());
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

    private JsonNode callGemini(AnalysisAIRequest request, String resolvedApiKey, String resolvedModel, double resolvedTemperature, int resolvedMaxOutputTokens) {
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", request.systemPrompt()))
                ),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", buildUserPrompt(request)))
                )),
                "generationConfig", Map.of(
                        "temperature", resolvedTemperature,
                        "topP", 0.2,
                        "topK", 1,
                        "candidateCount", 1,
                        "maxOutputTokens", resolvedMaxOutputTokens,
                        "responseMimeType", "application/json",
                        "responseSchema", analysisResponseSchema()
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
            throw new AnalysisAIProviderException(
                    AiErrorSanitizer.providerHttpError("analysis", exception),
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
                - Responde en espanol y no repitas palabras o fragmentos.
                - Prioriza brevedad: cada string debe ser una frase corta.
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
            String diagnostics = geminiDiagnostics(response);
            log.warn("Gemini analysis response does not contain text. diagnostics='{}'", diagnostics);
            throw new AnalysisAIProviderException("Gemini response does not contain candidates[0].content.parts[0].text; " + diagnostics);
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
                    stringList(root, "affectedGroups"),
                    stringList(root, "recommendedMonitoring"),
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

    private boolean isRecitationFailure(AnalysisAIProviderException exception) {
        return exception.getMessage() != null && exception.getMessage().contains(RECITATION_FINISH_REASON);
    }

    private boolean isRecitationFallbackRequest(AnalysisAIRequest request) {
        return request.userPrompt() != null && request.userPrompt().contains("bloqueado el intento anterior por RECITATION");
    }

    private AnalysisAIResponse conservativeRecitationFallback(AnalysisAIRequest request, String resolvedModel) {
        String topic = recitationSafeTopic(request);
        return new AnalysisAIResponse(
                "Analisis limitado por bloqueo del proveedor IA; se trabaja con metadatos y URL oficial del evento.",
                "Conviene revisar la resolucion oficial antes de elaborar posicionamiento o contenido definitivo.",
                List.of(topic, "La noticia procede de una fuente con enlace oficial disponible."),
                List.of("No se dispone del texto completo extraido del PDF en el contexto del analisis."),
                List.of("Usar la URL oficial como referencia para la revision humana y el seguimiento sindical."),
                affectedGroupsFor(request),
                List.of("Revisar el PDF oficial enlazado y confirmar plazos, colectivos y requisitos antes de publicar."),
                resolvedModel + ":conservative-recitation-fallback"
        );
    }

    private List<String> affectedGroupsFor(AnalysisAIRequest request) {
        if (request.category() == null) {
            return List.of();
        }
        return switch (request.category()) {
            case INTERINOS -> List.of("Personal interino o aspirante vinculado a bolsas");
            case OPOSICIONES -> List.of("Aspirantes a procesos selectivos");
            default -> List.of();
        };
    }

    private AnalysisAIRequest recitationSafeRequest(AnalysisAIRequest request) {
        return new AnalysisAIRequest(
                request.eventId(),
                request.eventTitle(),
                request.eventDescription(),
                request.category(),
                request.importance(),
                request.analysisType(),
                request.news(),
                request.systemPrompt(),
                recitationSafeUserPrompt(request)
        );
    }

    private String recitationSafeUserPrompt(AnalysisAIRequest request) {
        StringBuilder newsContext = new StringBuilder();
        if (request.news() == null || request.news().isEmpty()) {
            newsContext.append("Sin noticias asociadas.");
        } else {
            for (var item : request.news().stream().limit(5).toList()) {
                newsContext.append("- id: ").append(item.id()).append('\n')
                        .append("  fuente: ").append(safe(item.sourceName())).append('\n')
                        .append("  url: ").append(safe(item.url())).append('\n')
                        .append("  resumen_disponible: ").append(safe(item.summary())).append('\n')
                        .append("  publicado: ").append(item.publishedAt()).append("\n\n");
            }
        }

        return """
                EVENTO:
                id: %s
                tema_operativo: %s
                categoria: %s
                importancia: %s
                tipo_analisis: %s

                NOTICIAS:
                %s

                Genera un objeto JSON con exactamente esta estructura:
                {
                  "executiveSummary": "",
                  "unionSummary": "",
                  "keyPoints": [],
                  "risks": [],
                  "opportunities": [],
                  "affectedGroups": [],
                  "recommendedMonitoring": []
                }

                Reglas especificas para este reintento:
                - El proveedor ha bloqueado el intento anterior por RECITATION.
                - No reproduzcas literalmente titulos oficiales, fragmentos normativos ni texto de documentos.
                - Usa solo el tema operativo, los metadatos disponibles y las URL oficiales.
                - Si falta el texto completo del documento, declaralo como limitacion.
                - Mantente prudente y orientado a seguimiento sindical.
                """.formatted(
                request.eventId(),
                recitationSafeTopic(request),
                request.category(),
                request.importance(),
                request.analysisType(),
                newsContext.toString().trim()
        );
    }

    private String recitationSafeTopic(AnalysisAIRequest request) {
        String title = safe(request.eventTitle());
        if (containsIgnoreCase(title, "bolsa") && containsIgnoreCase(title, "unica") && containsIgnoreCase(title, "plazo")) {
            return "Resolucion oficial BOJA sobre ampliacion de plazo de solicitudes para actualizar la Bolsa Unica Comun.";
        }
        if (containsIgnoreCase(title, "boja") || containsIgnoreCase(title, "resolucion")) {
            return "Resolucion oficial relacionada con " + request.category() + ".";
        }
        if (!title.isBlank() && title.length() <= 140) {
            return title;
        }
        return "Evento informativo relacionado con " + request.category() + ".";
    }

    private boolean containsIgnoreCase(String value, String fragment) {
        return normalizeForSearch(value).contains(normalizeForSearch(fragment));
    }

    private String normalizeForSearch(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value;
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

    private double effectiveTemperature(double configuredTemperature) {
        if (configuredTemperature < 0) {
            return 0;
        }

        return Math.min(configuredTemperature, MAX_EFFECTIVE_ANALYSIS_TEMPERATURE);
    }

    private int effectiveMaxOutputTokens(int configuredMaxOutputTokens) {
        return Math.max(configuredMaxOutputTokens, MIN_EFFECTIVE_ANALYSIS_OUTPUT_TOKENS);
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
