package es.sindicato.intelligence.auth.application;

public record LoginCommand(
        String email,
        String password
) {
    public LoginCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
    }
}
