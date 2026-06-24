package es.sindicato.intelligence.auth.application;

import java.time.OffsetDateTime;

public record RefreshTokenRecord(
        Long id,
        Long userId,
        String tokenId,
        String tokenHash,
        OffsetDateTime issuedAt,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        OffsetDateTime replacedAt
) {
    public boolean isActive(OffsetDateTime now) {
        return revokedAt == null && replacedAt == null && expiresAt.isAfter(now);
    }
}
