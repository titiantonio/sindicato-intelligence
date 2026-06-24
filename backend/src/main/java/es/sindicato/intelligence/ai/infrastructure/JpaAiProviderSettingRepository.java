package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.core.security.SecretTextCipher;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAiProviderSettingRepository implements AiProviderSettingRepository {

    private final EntityManager entityManager;
    private final SecretTextCipher secretTextCipher;

    public JpaAiProviderSettingRepository(EntityManager entityManager, SecretTextCipher secretTextCipher) {
        this.entityManager = entityManager;
        this.secretTextCipher = secretTextCipher;
    }

    @Override
    public AiProviderSetting save(AiProviderSetting setting) {
        return toDomain(entityManager.merge(toEntity(setting)));
    }

    @Override
    public List<AiProviderSetting> findAll() {
        return entityManager.createQuery(
                        "SELECT setting FROM AiProviderSettingEntity setting ORDER BY setting.providerCode",
                        AiProviderSettingEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<AiProviderSetting> findByCode(String providerCode) {
        return Optional.ofNullable(entityManager.find(AiProviderSettingEntity.class, providerCode))
                .map(this::toDomain);
    }

    private AiProviderSettingEntity toEntity(AiProviderSetting setting) {
        return new AiProviderSettingEntity(
                setting.getProviderCode(),
                setting.getDisplayName(),
                setting.isEnabled(),
                secretTextCipher.encrypt(setting.getApiKey()),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }

    private AiProviderSetting toDomain(AiProviderSettingEntity entity) {
        return new AiProviderSetting(
                entity.getProviderCode(),
                entity.getDisplayName(),
                entity.isEnabled(),
                secretTextCipher.decryptIfNeeded(entity.getApiKeyEncrypted()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
