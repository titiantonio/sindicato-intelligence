package es.sindicato.intelligence.publication.domain;

import java.util.Optional;

public interface TelegramPublicationSettingsRepository {

    TelegramPublicationSettings save(TelegramPublicationSettings settings);

    Optional<TelegramPublicationSettings> find();
}
