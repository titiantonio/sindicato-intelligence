package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UpdateTelegramPublicationSettingsUseCase {

    private final TelegramPublicationSettingsRepository repository;

    public UpdateTelegramPublicationSettingsUseCase(TelegramPublicationSettingsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TelegramPublicationSettings execute(UpdateTelegramPublicationSettingsCommand command) {
        TelegramPublicationSettings settings = repository.find()
                .orElseThrow(() -> new IllegalStateException("telegram publication settings not found"));
        OffsetDateTime now = OffsetDateTime.now();
        List<TelegramPublicationDestination> destinations = toDestinations(command, now);
        settings.update(
                command.enabled(),
                command.baseUrl(),
                command.botToken(),
                primaryChatId(command.chatId(), destinations),
                command.disableWebPagePreview(),
                destinations,
                now
        );
        return repository.save(settings);
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
