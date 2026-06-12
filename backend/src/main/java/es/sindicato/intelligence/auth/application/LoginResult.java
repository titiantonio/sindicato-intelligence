package es.sindicato.intelligence.auth.application;

public record LoginResult(
        String accessToken,
        String refreshToken,
        Long userId,
        String userName,
        String userRole,
        boolean mustChangePassword
) {
}
