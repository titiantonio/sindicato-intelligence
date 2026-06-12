package es.sindicato.intelligence.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestTemporaryPasswordRequest(
        @NotBlank @Email String email
) {
}
