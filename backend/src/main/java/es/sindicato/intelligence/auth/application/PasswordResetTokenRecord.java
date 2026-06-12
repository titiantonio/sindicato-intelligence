package es.sindicato.intelligence.auth.application;

import java.time.OffsetDateTime;

public record PasswordResetTokenRecord(
        Long id,
        Long userId,
        String token,
        OffsetDateTime expiresAt,
        OffsetDateTime usedAt
) {

    public boolean isExpired(OffsetDateTime now) {
        return expiresAt.isBefore(now);
    }

    public boolean isUsed() {
        return usedAt != null;
    }
}
