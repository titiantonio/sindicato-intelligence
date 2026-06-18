package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiPromptVersion;
import es.sindicato.intelligence.ai.domain.AiPromptVersionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaAiPromptVersionRepository implements AiPromptVersionRepository {

    private final EntityManager entityManager;

    public JpaAiPromptVersionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<AiPromptVersion> findActive() {
        return entityManager.createQuery(
                        """
                        SELECT prompt
                        FROM AiPromptVersionEntity prompt
                        WHERE prompt.active = true
                        ORDER BY prompt.promptKey ASC
                        """,
                        AiPromptVersionEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AiPromptVersion toDomain(AiPromptVersionEntity entity) {
        return new AiPromptVersion(
                entity.getId(),
                entity.getPromptKey(),
                entity.getPromptName(),
                entity.getModule(),
                entity.getVersion(),
                entity.getChecksum(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
