package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserRole;

public record CreateUserCommand(
        String email,
        String name,
        UserRole role
) {
}
