package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.publication.application.PublishingProvider;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.application.PublishingRequest;
import es.sindicato.intelligence.publication.application.PublishingResult;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
@Order(100)
public class TelegramPublisher implements PublishingProvider {

    private static final Logger log = LoggerFactory.getLogger(TelegramPublisher.class);
    private static final String CHANNEL = "TELEGRAM";

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;
    private final TelegramPublicationSettingsRepository settingsRepository;

    public TelegramPublisher(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            TelegramPublicationSettingsRepository settingsRepository
    ) {
        this.restClientBuilder = restClientBuilder;
        this.objectMapper = objectMapper;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public boolean supports(String channel) {
        return CHANNEL.equalsIgnoreCase(channel);
    }

    @Override
    public PublishingResult publish(PublishingRequest request) {
        TelegramPublicationSettings settings = settingsRepository.find()
                .orElseThrow(() -> new PublishingProviderException("Telegram settings are not configured"));

        if (!settings.isEnabled()) {
            throw new PublishingProviderException("Telegram publication is disabled");
        }

        String botToken = requireText(settings.getBotToken(), "Telegram bot token is required");
        String chatId = requireText(settings.getChatId(), "Telegram chat id is required");
        RestClient restClient = restClientBuilder.clone().baseUrl(settings.getBaseUrl()).build();

        log.info("telegram publication request started: contentId={}, channel={}", request.contentId(), request.channel());

        try {
            JsonNode response = restClient.post()
                    .uri("/bot{token}/sendMessage", botToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "chat_id", chatId,
                            "text", buildTelegramMessage(request),
                            "disable_web_page_preview", settings.isDisableWebPagePreview()
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            PublishingResult result = parseResponse(request, response);
            log.info("telegram publication request completed: contentId={}, externalId={}", request.contentId(), result.externalId());
            return result;
        } catch (RestClientResponseException exception) {
            String responsePayload = errorPayload(exception.getStatusCode().value(), extractDescription(exception.getResponseBodyAsString()));
            log.error("telegram publication request failed: contentId={}, statusCode={}, reason={}", request.contentId(), exception.getStatusCode().value(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed: " + responsePayload, exception);
        } catch (RestClientException exception) {
            log.error("telegram publication request failed: contentId={}, reason={}", request.contentId(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed", exception);
        }
    }

    private PublishingResult parseResponse(PublishingRequest request, JsonNode response) {
        if (response == null || !response.path("ok").asBoolean(false)) {
            String description = response == null ? "missing response" : response.path("description").asText("unexpected response");
            log.warn("telegram publication response rejected: contentId={}, reason={}", request.contentId(), description);
            throw new PublishingProviderException("Telegram publication rejected: " + errorPayload(null, description));
        }

        JsonNode messageIdNode = response.at("/result/message_id");
        if (!messageIdNode.canConvertToLong()) {
            log.warn("telegram publication response without message id: contentId={}", request.contentId());
            throw new PublishingProviderException("Telegram response does not contain message_id");
        }

        String messageId = messageIdNode.asText();
        return new PublishingResult(messageId, successPayload(messageId));
    }

    private String buildTelegramMessage(PublishingRequest request) {
        return request.title() + "\n\n" + request.message();
    }

    private String successPayload(String messageId) {
        return toJson(Map.of("ok", true, "messageId", messageId));
    }

    private String errorPayload(Integer statusCode, String description) {
        if (statusCode == null) {
            return toJson(Map.of("ok", false, "description", description));
        }

        return toJson(Map.of("ok", false, "statusCode", statusCode, "description", description));
    }

    private String extractDescription(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "empty response body";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("description").asText("unexpected response");
        } catch (JsonProcessingException exception) {
            return "unexpected response";
        }
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new PublishingProviderException("Telegram response payload cannot be serialized", exception);
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new PublishingProviderException(message);
        }

        return value;
    }
}
