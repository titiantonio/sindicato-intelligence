package es.sindicato.intelligence.auth.application;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository {

    PasswordResetTokenRecord create(Long userId, String token, OffsetDateTime expiresAt);

    Optional<PasswordResetTokenRecord> findByToken(String token);

    void markAsUsed(Long tokenId, OffsetDateTime usedAt);

    void invalidateActiveTokensForUser(Long userId, OffsetDateTime usedAt);
}
