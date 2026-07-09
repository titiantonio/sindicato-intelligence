package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.publication.application.ManualPublishingProvider;
import es.sindicato.intelligence.publication.application.ManualPublishingRequest;
import es.sindicato.intelligence.publication.application.PublishingProvider;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.application.PublishingRequest;
import es.sindicato.intelligence.publication.application.PublishingResult;
import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationMediaType;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(100)
public class TelegramPublisher implements PublishingProvider, ManualPublishingProvider {

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
        TelegramPublicationSettings settings = configuredSettings();
        String botToken = requireText(settings.getBotToken(), "Telegram bot token is required");
        List<TelegramPublicationDestination> destinations = defaultDestinations(settings);
        RestClient restClient = restClientBuilder.clone().baseUrl(settings.getBaseUrl()).build();

        log.info("telegram publication request started: contentId={}, channel={}, destinations={}", request.contentId(), request.channel(), destinations.size());

        List<String> externalIds = new ArrayList<>();
        List<Map<String, Object>> targetResults = new ArrayList<>();
        for (TelegramPublicationDestination destination : destinations) {
            PublishingResult result = sendText(
                    restClient,
                    botToken,
                    destination.getChatId(),
                    buildTelegramMessage(request),
                    settings.isDisableWebPagePreview(),
                    request.contentId()
            );
            externalIds.add(result.externalId());
            targetResults.add(Map.of(
                    "destinationId", destination.getId() == null ? "" : destination.getId(),
                    "destinationName", destination.getName(),
                    "messageId", result.externalId()
            ));
        }

        PublishingResult result = new PublishingResult(String.join(",", externalIds), toJson(Map.of("ok", true, "targets", targetResults)));
        log.info("telegram publication request completed: contentId={}, externalId={}", request.contentId(), result.externalId());
        return result;
    }

    @Override
    public PublishingResult publishManual(ManualPublishingRequest request) {
        TelegramPublicationSettings settings = configuredSettings();
        String botToken = requireText(settings.getBotToken(), "Telegram bot token is required");
        RestClient restClient = restClientBuilder.clone().baseUrl(settings.getBaseUrl()).build();
        String chatId = requireText(request.target().getDestinationAddress(), "Telegram chat id is required");
        List<String> messageIds = new ArrayList<>();

        if (hasText(request.message())) {
            PublishingResult textResult = sendText(
                    restClient,
                    botToken,
                    chatId,
                    buildManualText(request),
                    settings.isDisableWebPagePreview(),
                    request.publicationId()
            );
            messageIds.add(textResult.externalId());
        }

        if (canSendMediaGroup(request.attachments())) {
            PublishingResult mediaGroupResult = sendMediaGroup(restClient, botToken, chatId, request.attachments(), attachmentCaption(request), request.publicationId());
            messageIds.add(mediaGroupResult.externalId());
        } else {
            for (PublicationAttachment attachment : request.attachments()) {
                PublishingResult attachmentResult = sendAttachment(restClient, botToken, chatId, attachment, attachmentCaption(request), request.publicationId());
                messageIds.add(attachmentResult.externalId());
            }
        }

        if (messageIds.isEmpty()) {
            throw new PublishingProviderException("Telegram manual publication requires text or attachment");
        }

        return new PublishingResult(String.join(",", messageIds), toJson(Map.of("ok", true, "messageIds", messageIds)));
    }

    private TelegramPublicationSettings configuredSettings() {
        TelegramPublicationSettings settings = settingsRepository.find()
                .orElseThrow(() -> new PublishingProviderException("Telegram settings are not configured"));
        if (!settings.isEnabled()) {
            throw new PublishingProviderException("Telegram publication is disabled");
        }
        return settings;
    }

    private PublishingResult sendText(RestClient restClient, String botToken, String chatId, String text, boolean disableWebPagePreview, Long logEntityId) {
        try {
            JsonNode response = restClient.post()
                    .uri("/bot{token}/sendMessage", botToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(textMessagePayload(chatId, text, disableWebPagePreview))
                    .retrieve()
                    .body(JsonNode.class);
            return parseResponse(logEntityId, response);
        } catch (RestClientResponseException exception) {
            String responsePayload = errorPayload(exception.getStatusCode().value(), extractDescription(exception.getResponseBodyAsString()));
            log.error("telegram text request failed: entityId={}, statusCode={}, reason={}", logEntityId, exception.getStatusCode().value(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed: " + responsePayload, exception);
        } catch (RestClientException exception) {
            log.error("telegram text request failed: entityId={}, reason={}", logEntityId, exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed", exception);
        }
    }

    private PublishingResult sendAttachment(RestClient restClient, String botToken, String chatId, PublicationAttachment attachment, String caption, Long logEntityId) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", chatId);
            body.add(telegramFileField(attachment), new FileSystemResource(Path.of(attachment.getStoragePath())));
            if (hasText(caption)) {
                body.add("caption", caption);
                body.add("parse_mode", "HTML");
            }
            JsonNode response = restClient.post()
                    .uri("/bot{token}/{method}", botToken, attachment.getTelegramMethod())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            return parseResponse(logEntityId, response);
        } catch (RestClientResponseException exception) {
            String responsePayload = errorPayload(exception.getStatusCode().value(), extractDescription(exception.getResponseBodyAsString()));
            log.error("telegram attachment request failed: entityId={}, method={}, statusCode={}, reason={}", logEntityId, attachment.getTelegramMethod(), exception.getStatusCode().value(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed: " + responsePayload, exception);
        } catch (RestClientException exception) {
            log.error("telegram attachment request failed: entityId={}, method={}, reason={}", logEntityId, attachment.getTelegramMethod(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed", exception);
        }
    }

    private PublishingResult sendMediaGroup(RestClient restClient, String botToken, String chatId, List<PublicationAttachment> attachments, String caption, Long logEntityId) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", chatId);
            java.util.ArrayList<Map<String, Object>> media = new java.util.ArrayList<>();
            for (int index = 0; index < attachments.size(); index++) {
                PublicationAttachment attachment = attachments.get(index);
                String fieldName = "file" + index;
                body.add(fieldName, new FileSystemResource(Path.of(attachment.getStoragePath())));
                java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                item.put("type", telegramMediaGroupType(attachment));
                item.put("media", "attach://" + fieldName);
                if (index == 0 && hasText(caption)) {
                    item.put("caption", caption);
                    item.put("parse_mode", "HTML");
                }
                media.add(item);
            }
            body.add("media", objectMapper.writeValueAsString(media));
            JsonNode response = restClient.post()
                    .uri("/bot{token}/sendMediaGroup", botToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            return parseMediaGroupResponse(logEntityId, response);
        } catch (JsonProcessingException exception) {
            throw new PublishingProviderException("Telegram media group payload cannot be serialized", exception);
        } catch (RestClientResponseException exception) {
            String responsePayload = errorPayload(exception.getStatusCode().value(), extractDescription(exception.getResponseBodyAsString()));
            log.error("telegram media group request failed: entityId={}, statusCode={}, reason={}", logEntityId, exception.getStatusCode().value(), exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed: " + responsePayload, exception);
        } catch (RestClientException exception) {
            log.error("telegram media group request failed: entityId={}, reason={}", logEntityId, exception.getMessage(), exception);
            throw new PublishingProviderException("Telegram publication failed", exception);
        }
    }

    private PublishingResult parseResponse(Long logEntityId, JsonNode response) {
        if (response == null || !response.path("ok").asBoolean(false)) {
            String description = response == null ? "missing response" : response.path("description").asText("unexpected response");
            log.warn("telegram publication response rejected: entityId={}, reason={}", logEntityId, description);
            throw new PublishingProviderException("Telegram publication rejected: " + errorPayload(null, description));
        }

        JsonNode messageIdNode = response.at("/result/message_id");
        if (!messageIdNode.canConvertToLong()) {
            log.warn("telegram publication response without message id: entityId={}", logEntityId);
            throw new PublishingProviderException("Telegram response does not contain message_id");
        }

        String messageId = messageIdNode.asText();
        return new PublishingResult(messageId, toJson(Map.of("ok", true, "messageId", messageId)));
    }

    private PublishingResult parseMediaGroupResponse(Long logEntityId, JsonNode response) {
        if (response == null || !response.path("ok").asBoolean(false) || !response.path("result").isArray()) {
            String description = response == null ? "missing response" : response.path("description").asText("unexpected response");
            log.warn("telegram media group response rejected: entityId={}, reason={}", logEntityId, description);
            throw new PublishingProviderException("Telegram publication rejected: " + errorPayload(null, description));
        }
        List<String> messageIds = new ArrayList<>();
        for (JsonNode message : response.path("result")) {
            JsonNode messageIdNode = message.path("message_id");
            if (messageIdNode.canConvertToLong()) {
                messageIds.add(messageIdNode.asText());
            }
        }
        if (messageIds.isEmpty()) {
            throw new PublishingProviderException("Telegram response does not contain message_id");
        }
        return new PublishingResult(String.join(",", messageIds), toJson(Map.of("ok", true, "messageIds", messageIds)));
    }

    private String buildTelegramMessage(PublishingRequest request) {
        return sanitizeTelegramHtml(request.title()) + "\n\n" + sanitizeTelegramHtml(request.message());
    }

    private String buildManualText(ManualPublishingRequest request) {
        if (hasText(request.title())) {
            return sanitizeTelegramHtml(request.title()) + "\n\n" + sanitizeTelegramHtml(request.message());
        }
        return sanitizeTelegramHtml(request.message());
    }

    private String attachmentCaption(ManualPublishingRequest request) {
        if (hasText(request.title()) && hasText(request.message())) {
            return sanitizeTelegramHtml(request.title()) + "\n\n" + sanitizeTelegramHtml(request.message());
        }
        if (hasText(request.message())) {
            return sanitizeTelegramHtml(request.message());
        }
        return hasText(request.title()) ? sanitizeTelegramHtml(request.title()) : null;
    }

    private List<TelegramPublicationDestination> defaultDestinations(TelegramPublicationSettings settings) {
        List<TelegramPublicationDestination> destinations = settings.defaultDestinations();
        if (!destinations.isEmpty()) {
            return destinations;
        }
        String legacyChatId = requireText(settings.getChatId(), "Telegram chat id is required");
        return List.of(TelegramPublicationDestination.newDestination("Principal", legacyChatId, true, true, OffsetDateTime.now()));
    }

    private String telegramFileField(PublicationAttachment attachment) {
        return switch (attachment.getMediaType()) {
            case IMAGE -> "photo";
            case VIDEO -> "video";
            case AUDIO -> "audio";
            case DOCUMENT -> "document";
        };
    }

    private boolean canSendMediaGroup(List<PublicationAttachment> attachments) {
        return attachments.size() > 1
                && attachments.stream().allMatch(attachment -> attachment.getMediaType() == PublicationMediaType.IMAGE || attachment.getMediaType() == PublicationMediaType.VIDEO);
    }

    private String telegramMediaGroupType(PublicationAttachment attachment) {
        return attachment.getMediaType() == PublicationMediaType.VIDEO ? "video" : "photo";
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
        if (!hasText(value)) {
            throw new PublishingProviderException(message);
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Map<String, Object> textMessagePayload(String chatId, String text, boolean disableWebPagePreview) {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", text);
        payload.put("disable_web_page_preview", disableWebPagePreview);
        payload.put("parse_mode", "HTML");
        return payload;
    }

    private String sanitizeTelegramHtml(String value) {
        if (!hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.replaceAll("(?is)<(?!/?(?:b|strong|i|em|u|ins|s|strike|del|code|pre)>|a\\s+href=\"https?://[^\"]+\">|/a>)[^>]*>", "");
    }
}
