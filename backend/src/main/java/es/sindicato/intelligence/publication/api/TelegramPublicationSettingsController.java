package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.application.GetTelegramPublicationSettingsUseCase;
import es.sindicato.intelligence.publication.application.TelegramPublicationDestinationCommand;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsCommand;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsUseCase;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings/telegram")
public class TelegramPublicationSettingsController {

    private final GetTelegramPublicationSettingsUseCase getSettingsUseCase;
    private final UpdateTelegramPublicationSettingsUseCase updateSettingsUseCase;

    public TelegramPublicationSettingsController(
            GetTelegramPublicationSettingsUseCase getSettingsUseCase,
            UpdateTelegramPublicationSettingsUseCase updateSettingsUseCase
    ) {
        this.getSettingsUseCase = getSettingsUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
    }

    @GetMapping
    public TelegramPublicationSettingsResponse getSettings() {
        return toResponse(getSettingsUseCase.execute());
    }

    @PutMapping
    public TelegramPublicationSettingsResponse updateSettings(@Valid @RequestBody UpdateTelegramPublicationSettingsRequest request) {
        return toResponse(updateSettingsUseCase.execute(new UpdateTelegramPublicationSettingsCommand(
                request.enabled(),
                request.baseUrl(),
                request.botToken(),
                request.chatId(),
                request.disableWebPagePreview(),
                valueOrDefault(request.maxAttachmentCount(), TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_COUNT),
                valueOrDefault(request.maxAttachmentFileBytes(), TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_FILE_BYTES),
                valueOrDefault(request.maxAttachmentTotalBytes(), TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES),
                toDestinationCommands(request)
        )));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private TelegramPublicationSettingsResponse toResponse(TelegramPublicationSettings settings) {
        return new TelegramPublicationSettingsResponse(
                settings.isEnabled(),
                settings.getBaseUrl(),
                settings.getChatId(),
                settings.isDisableWebPagePreview(),
                settings.getBotToken() != null,
                tokenPreview(settings.getBotToken()),
                settings.isReadyToPublish(),
                settings.getMaxAttachmentCount(),
                settings.getMaxAttachmentFileBytes(),
                settings.getMaxAttachmentTotalBytes(),
                settings.getUpdatedAt(),
                settings.getDestinations().stream()
                        .map(this::toDestinationResponse)
                        .toList()
        );
    }

    private java.util.List<TelegramPublicationDestinationCommand> toDestinationCommands(UpdateTelegramPublicationSettingsRequest request) {
        if (request.destinations() == null) {
            return java.util.List.of();
        }
        return request.destinations().stream()
                .map(destination -> new TelegramPublicationDestinationCommand(
                        destination.id(),
                        destination.name(),
                        destination.chatId(),
                        Boolean.TRUE.equals(destination.active()),
                        Boolean.TRUE.equals(destination.defaultSelected())
                ))
                .toList();
    }

    private TelegramPublicationDestinationResponse toDestinationResponse(TelegramPublicationDestination destination) {
        return new TelegramPublicationDestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getChatId(),
                destination.isActive(),
                destination.isDefaultSelected(),
                destination.getUpdatedAt()
        );
    }

    private String tokenPreview(String botToken) {
        if (botToken == null || botToken.isBlank()) {
            return null;
        }
        String trimmed = botToken.trim();
        if (trimmed.length() <= 8) {
            return "********";
        }
        return trimmed.substring(0, 4) + "..." + trimmed.substring(trimmed.length() - 4);
    }

    private int valueOrDefault(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private long valueOrDefault(Long value, long defaultValue) {
        return value == null ? defaultValue : value;
    }
}
