package es.sindicato.intelligence.auth.infrastructure;

import es.sindicato.intelligence.auth.application.RefreshTokenRecord;
import es.sindicato.intelligence.auth.application.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private final EntityManager entityManager;

    public JpaRefreshTokenRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public RefreshTokenRecord create(Long userId, String tokenId, String tokenHash, OffsetDateTime issuedAt, OffsetDateTime expiresAt) {
        RefreshTokenEntity entity = new RefreshTokenEntity(null, userId, tokenId, tokenHash, issuedAt, expiresAt, null, null);
        entityManager.persist(entity);
        return toRecord(entity);
    }

    @Override
    public Optional<RefreshTokenRecord> findByTokenId(String tokenId) {
        return entityManager.createQuery(
                        "SELECT t FROM RefreshTokenEntity t WHERE t.tokenId = :tokenId",
                        RefreshTokenEntity.class
                )
                .setParameter("tokenId", tokenId)
                .getResultStream()
                .findFirst()
                .map(this::toRecord);
    }

    @Override
    public void markAsReplaced(Long tokenRecordId, OffsetDateTime replacedAt) {
        RefreshTokenEntity entity = entityManager.find(RefreshTokenEntity.class, tokenRecordId);
        if (entity != null) {
            entity.setReplacedAt(replacedAt);
        }
    }

    @Override
    public void revokeActiveTokensForUser(Long userId, OffsetDateTime revokedAt) {
        entityManager.createQuery(
                        "UPDATE RefreshTokenEntity t SET t.revokedAt = :revokedAt " +
                                "WHERE t.userId = :userId AND t.revokedAt IS NULL AND t.replacedAt IS NULL"
                )
                .setParameter("revokedAt", revokedAt)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    private RefreshTokenRecord toRecord(RefreshTokenEntity entity) {
        return new RefreshTokenRecord(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenId(),
                entity.getTokenHash(),
                entity.getIssuedAt(),
                entity.getExpiresAt(),
                entity.getRevokedAt(),
                entity.getReplacedAt()
        );
    }
}
