package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

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
        settings.update(
                command.enabled(),
                command.baseUrl(),
                command.botToken(),
                command.chatId(),
                command.disableWebPagePreview(),
                OffsetDateTime.now()
        );
        return repository.save(settings);
    }
}
