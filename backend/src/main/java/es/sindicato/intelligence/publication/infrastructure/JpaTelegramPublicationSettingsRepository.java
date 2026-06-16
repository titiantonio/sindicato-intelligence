package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTelegramPublicationSettingsRepository implements TelegramPublicationSettingsRepository {

    private static final short SETTINGS_ID = 1;

    private final EntityManager entityManager;

    public JpaTelegramPublicationSettingsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TelegramPublicationSettings save(TelegramPublicationSettings settings) {
        return toDomain(entityManager.merge(toEntity(settings)));
    }

    @Override
    public Optional<TelegramPublicationSettings> find() {
        return Optional.ofNullable(entityManager.find(TelegramPublicationSettingsEntity.class, SETTINGS_ID))
                .map(this::toDomain);
    }

    private TelegramPublicationSettingsEntity toEntity(TelegramPublicationSettings settings) {
        return new TelegramPublicationSettingsEntity(
                settings.getId(),
                settings.isEnabled(),
                settings.getBaseUrl(),
                settings.getBotToken(),
                settings.getChatId(),
                settings.isDisableWebPagePreview(),
                settings.getCreatedAt(),
                settings.getUpdatedAt()
        );
    }

    private TelegramPublicationSettings toDomain(TelegramPublicationSettingsEntity entity) {
        return new TelegramPublicationSettings(
                entity.getId(),
                entity.isEnabled(),
                entity.getBaseUrl(),
                entity.getBotToken(),
                entity.getChatId(),
                entity.isDisableWebPagePreview(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
