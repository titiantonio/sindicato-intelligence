package es.sindicato.intelligence.auth.application;

public record AuthenticatedUser(
        Long id,
        String email,
        String name,
        String role
) {
    public AuthenticatedUser {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("role is required");
        }
    }
}
