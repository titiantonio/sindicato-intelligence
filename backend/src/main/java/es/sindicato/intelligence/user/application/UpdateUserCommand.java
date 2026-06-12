package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserRole;

public record UpdateUserCommand(
        String name,
        UserRole role
) {
}
