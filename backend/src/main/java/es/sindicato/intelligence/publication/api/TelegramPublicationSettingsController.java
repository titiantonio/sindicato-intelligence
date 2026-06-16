package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.application.GetTelegramPublicationSettingsUseCase;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsCommand;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsUseCase;
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
                request.disableWebPagePreview()
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
                settings.getUpdatedAt()
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
}
