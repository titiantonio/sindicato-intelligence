package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAiWorkflowSettingRepository implements AiWorkflowSettingRepository {

    private final EntityManager entityManager;

    public JpaAiWorkflowSettingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public AiWorkflowSetting save(AiWorkflowSetting setting) {
        return toDomain(entityManager.merge(toEntity(setting)));
    }

    @Override
    public List<AiWorkflowSetting> findAll() {
        return entityManager.createQuery(
                        "SELECT setting FROM AiWorkflowSettingEntity setting ORDER BY setting.workflowCode",
                        AiWorkflowSettingEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<AiWorkflowSetting> findByWorkflowCode(String workflowCode) {
        return Optional.ofNullable(entityManager.find(AiWorkflowSettingEntity.class, workflowCode))
                .map(this::toDomain);
    }

    private AiWorkflowSettingEntity toEntity(AiWorkflowSetting setting) {
        return new AiWorkflowSettingEntity(
                setting.getWorkflowCode(),
                setting.getProviderCode(),
                setting.getModelName(),
                setting.getTemperature(),
                setting.getMaxOutputTokens(),
                setting.getCooldownSeconds(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }

    private AiWorkflowSetting toDomain(AiWorkflowSettingEntity entity) {
        return new AiWorkflowSetting(
                entity.getWorkflowCode(),
                entity.getProviderCode(),
                entity.getModelName(),
                entity.getTemperature(),
                entity.getMaxOutputTokens(),
                entity.getCooldownSeconds(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
