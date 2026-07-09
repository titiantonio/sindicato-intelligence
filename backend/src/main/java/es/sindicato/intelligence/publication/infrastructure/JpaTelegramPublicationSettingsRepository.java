package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.core.security.SecretTextCipher;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaTelegramPublicationSettingsRepository implements TelegramPublicationSettingsRepository {

    private static final short SETTINGS_ID = 1;

    private final EntityManager entityManager;
    private final SecretTextCipher secretTextCipher;

    public JpaTelegramPublicationSettingsRepository(EntityManager entityManager, SecretTextCipher secretTextCipher) {
        this.entityManager = entityManager;
        this.secretTextCipher = secretTextCipher;
    }

    @Override
    public TelegramPublicationSettings save(TelegramPublicationSettings settings) {
        TelegramPublicationSettingsEntity saved = entityManager.merge(toEntity(settings));
        if (!settings.getDestinations().isEmpty()) {
            entityManager.createQuery("DELETE FROM TelegramPublicationDestinationEntity destination WHERE destination.settingsId = :settingsId")
                    .setParameter("settingsId", settings.getId())
                    .executeUpdate();

            OffsetDateTime now = OffsetDateTime.now();
            for (TelegramPublicationDestination destination : settings.getDestinations()) {
                OffsetDateTime createdAt = destination.getCreatedAt() == null ? now : destination.getCreatedAt();
                OffsetDateTime updatedAt = destination.getUpdatedAt() == null ? now : destination.getUpdatedAt();
                entityManager.persist(new TelegramPublicationDestinationEntity(
                        null,
                        settings.getId(),
                        destination.getName(),
                        destination.getChatId(),
                        destination.isActive(),
                        destination.isDefaultSelected(),
                        createdAt,
                        updatedAt
                ));
            }
            entityManager.flush();
        }
        return toDomain(saved);
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
                secretTextCipher.encrypt(settings.getBotToken()),
                settings.getChatId(),
                settings.isDisableWebPagePreview(),
                settings.getMaxAttachmentCount(),
                settings.getMaxAttachmentFileBytes(),
                settings.getMaxAttachmentTotalBytes(),
                settings.getCreatedAt(),
                settings.getUpdatedAt()
        );
    }

    private TelegramPublicationSettings toDomain(TelegramPublicationSettingsEntity entity) {
        List<TelegramPublicationDestination> destinations = findDestinations(entity.getId());
        return new TelegramPublicationSettings(
                entity.getId(),
                entity.isEnabled(),
                entity.getBaseUrl(),
                secretTextCipher.decryptIfNeeded(entity.getBotToken()),
                entity.getChatId(),
                entity.isDisableWebPagePreview(),
                entity.getMaxAttachmentCount(),
                entity.getMaxAttachmentFileBytes(),
                entity.getMaxAttachmentTotalBytes(),
                destinations,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private List<TelegramPublicationDestination> findDestinations(short settingsId) {
        try {
            return entityManager.createQuery(
                            "SELECT destination FROM TelegramPublicationDestinationEntity destination WHERE destination.settingsId = :settingsId ORDER BY destination.id ASC",
                            TelegramPublicationDestinationEntity.class
                    )
                    .setParameter("settingsId", settingsId)
                    .getResultStream()
                    .map(entity -> new TelegramPublicationDestination(
                            entity.getId(),
                            entity.getName(),
                            entity.getChatId(),
                            entity.isActive(),
                            entity.isDefaultSelected(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt()
                    ))
                    .toList();
        } catch (NullPointerException exception) {
            return List.of();
        }
    }
}
