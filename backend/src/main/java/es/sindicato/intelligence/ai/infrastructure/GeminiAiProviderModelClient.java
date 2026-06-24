package es.sindicato.intelligence.ai.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import es.sindicato.intelligence.ai.application.AiErrorSanitizer;
import es.sindicato.intelligence.ai.application.AiModelOption;
import es.sindicato.intelligence.ai.application.AiProviderModelClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class GeminiAiProviderModelClient implements AiProviderModelClient {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    private final RestClient restClient;

    public GeminiAiProviderModelClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(BASE_URL).build();
    }

    @Override
    public boolean supports(String providerCode) {
        return "gemini".equalsIgnoreCase(providerCode);
    }

    @Override
    public List<AiModelOption> listModels(String apiKey) {
        try {
            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/models").queryParam("pageSize", 1000).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", apiKey)
                    .retrieve()
                    .body(JsonNode.class);
            return parseModels(response);
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(AiErrorSanitizer.providerHttpError("models", exception), exception);
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Gemini models request failed", exception);
        }
    }

    private List<AiModelOption> parseModels(JsonNode response) {
        List<AiModelOption> models = new ArrayList<>();
        JsonNode items = response == null ? null : response.get("models");
        if (items == null || !items.isArray()) {
            return models;
        }
        for (JsonNode item : items) {
            if (supportsGenerateContent(item)) {
                String name = item.path("name").asText("");
                if (!name.isBlank()) {
                    models.add(new AiModelOption(name, item.path("displayName").asText(name)));
                }
            }
        }
        return models.stream()
                .sorted(Comparator.comparing(AiModelOption::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private boolean supportsGenerateContent(JsonNode item) {
        JsonNode methods = item.get("supportedGenerationMethods");
        if (methods == null || !methods.isArray()) {
            return false;
        }
        for (JsonNode method : methods) {
            if ("generateContent".equals(method.asText())) {
                return true;
            }
        }
        return false;
    }
}
