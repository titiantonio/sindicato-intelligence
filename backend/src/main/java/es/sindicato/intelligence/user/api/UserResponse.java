package es.sindicato.intelligence.user.api;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String email,
        String name,
        String role,
        boolean active,
        boolean mustChangePassword,
        String status,
        OffsetDateTime temporaryPasswordExpiresAt,
        OffsetDateTime lastLoginAt,
        OffsetDateTime lastPasswordChangeAt
) {
}
