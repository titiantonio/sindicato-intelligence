package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.auth.application.PasswordResetTokenRecord;
import es.sindicato.intelligence.auth.application.PasswordResetTokenRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public class JpaPasswordResetTokenRepository implements PasswordResetTokenRepository {

    private final EntityManager entityManager;

    public JpaPasswordResetTokenRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PasswordResetTokenRecord create(Long userId, String token, OffsetDateTime expiresAt) {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity(null, userId, token, expiresAt, null);
        entityManager.persist(entity);
        return toRecord(entity);
    }

    @Override
    public Optional<PasswordResetTokenRecord> findByToken(String token) {
        return entityManager.createQuery(
                        "SELECT t FROM PasswordResetTokenEntity t WHERE t.token = :token",
                        PasswordResetTokenEntity.class
                )
                .setParameter("token", token)
                .getResultStream()
                .findFirst()
                .map(this::toRecord);
    }

    @Override
    public void markAsUsed(Long tokenId, OffsetDateTime usedAt) {
        PasswordResetTokenEntity entity = entityManager.find(PasswordResetTokenEntity.class, tokenId);
        if (entity != null) {
            entity.setUsedAt(usedAt);
        }
    }

    @Override
    public void invalidateActiveTokensForUser(Long userId, OffsetDateTime usedAt) {
        entityManager.createQuery(
                        "UPDATE PasswordResetTokenEntity t SET t.usedAt = :usedAt " +
                                "WHERE t.userId = :userId AND t.usedAt IS NULL"
                )
                .setParameter("usedAt", usedAt)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    private PasswordResetTokenRecord toRecord(PasswordResetTokenEntity entity) {
        return new PasswordResetTokenRecord(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.getUsedAt()
        );
    }
}
