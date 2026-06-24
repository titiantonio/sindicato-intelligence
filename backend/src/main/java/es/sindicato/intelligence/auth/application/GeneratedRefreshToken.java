package es.sindicato.intelligence.auth.application;

import java.time.Instant;

public record GeneratedRefreshToken(
        String value,
        String tokenId,
        Instant expiresAt
) {
}
