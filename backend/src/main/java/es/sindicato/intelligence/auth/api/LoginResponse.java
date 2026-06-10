package es.sindicato.intelligence.auth.api;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {

    public record UserResponse(
            Long id,
            String name,
            String role
    ) {
    }
}
