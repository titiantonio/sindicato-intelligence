package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UpdateTelegramPublicationSettingsUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateTelegramPublicationSettingsUseCase.class);

    private final TelegramPublicationSettingsRepository repository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public UpdateTelegramPublicationSettingsUseCase(TelegramPublicationSettingsRepository repository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.repository = repository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public TelegramPublicationSettings execute(UpdateTelegramPublicationSettingsCommand command) {
        log.info("telegram settings update started: enabled={}, clearBotToken={}", command.enabled(), command.clearBotToken());
        TelegramPublicationSettings settings = repository.find()
                .orElseThrow(() -> new IllegalStateException("telegram publication settings not found"));
        OffsetDateTime now = OffsetDateTime.now();
        String oldValues = telegramSettings(settings);
        List<TelegramPublicationDestination> destinations = toDestinations(command, now);
        settings.update(
                command.enabled(),
                command.baseUrl(),
                command.clearBotToken() ? "" : command.botToken(),
                primaryChatId(command.chatId(), destinations),
                command.disableWebPagePreview(),
                command.maxAttachmentCount(),
                command.maxAttachmentFileBytes(),
                command.maxAttachmentTotalBytes(),
                destinations,
                now
        );
        TelegramPublicationSettings savedSettings = repository.save(settings);
        recordAuditLogUseCase.record(
                "TELEGRAM_SETTINGS_UPDATED",
                "TELEGRAM_SETTINGS",
                (long) savedSettings.getId(),
                oldValues,
                telegramSettings(savedSettings)
        );
        log.info("telegram settings update completed: enabled={}, readyToPublish={}, destinations={}", savedSettings.isEnabled(), savedSettings.isReadyToPublish(), savedSettings.getDestinations().size());
        return savedSettings;
    }

    private String telegramSettings(TelegramPublicationSettings settings) {
        return AuditDetailFormatter.telegramSettingsUpdated(
                settings.isEnabled(),
                settings.getBaseUrl(),
                settings.isDisableWebPagePreview(),
                settings.getDestinations().size(),
                settings.getMaxAttachmentCount()
        );
    }

    private List<TelegramPublicationDestination> toDestinations(UpdateTelegramPublicationSettingsCommand command, OffsetDateTime now) {
        if (command.destinations() == null || command.destinations().isEmpty()) {
            if (command.chatId() == null || command.chatId().isBlank()) {
                return List.of();
            }
            return List.of(TelegramPublicationDestination.newDestination("Principal", command.chatId(), true, true, now));
        }
        return command.destinations().stream()
                .map(destination -> new TelegramPublicationDestination(
                        destination.id(),
                        destination.name(),
                        destination.chatId(),
                        destination.active(),
                        destination.defaultSelected(),
                        now,
                        now
                ))
                .toList();
    }

    private String primaryChatId(String legacyChatId, List<TelegramPublicationDestination> destinations) {
        if (legacyChatId != null && !legacyChatId.isBlank()) {
            return legacyChatId;
        }
        return destinations.stream()
                .filter(TelegramPublicationDestination::isActive)
                .findFirst()
                .map(TelegramPublicationDestination::getChatId)
                .orElse(null);
    }
}
