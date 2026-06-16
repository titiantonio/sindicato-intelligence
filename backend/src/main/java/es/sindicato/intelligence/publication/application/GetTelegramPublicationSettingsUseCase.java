package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTelegramPublicationSettingsUseCase {

    private final TelegramPublicationSettingsRepository repository;

    public GetTelegramPublicationSettingsUseCase(TelegramPublicationSettingsRepository repository) {
        this.repository = repository;
    }

    public TelegramPublicationSettings execute() {
        return repository.find()
                .orElseThrow(() -> new IllegalStateException("telegram publication settings not found"));
    }
}
