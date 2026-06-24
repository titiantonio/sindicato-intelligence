package es.sindicato.intelligence.auth.application;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshTokenRecord create(Long userId, String tokenId, String tokenHash, OffsetDateTime issuedAt, OffsetDateTime expiresAt);

    Optional<RefreshTokenRecord> findByTokenId(String tokenId);

    void markAsReplaced(Long tokenRecordId, OffsetDateTime replacedAt);

    void revokeActiveTokensForUser(Long userId, OffsetDateTime revokedAt);
}
