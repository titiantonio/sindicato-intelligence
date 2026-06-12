package es.sindicato.intelligence.user.api;

import es.sindicato.intelligence.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotNull UserRole role
) {
}
