package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RefreshTokenUseCaseTest {

    @Test
    void refreshesAccessAndRefreshTokens() {
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenUseCase useCase = new RefreshTokenUseCase(jwtDecoder, jwtTokenService, userRepository);
        UserAccount account = new UserAccount(1L, "admin@sindicato.es", "$2a$10$hash", "Admin Sindicato", UserRole.ADMIN, true, false);

        when(jwtDecoder.decode("refresh-token")).thenReturn(jwt("REFRESH"));
        when(userRepository.findByEmail("admin@sindicato.es")).thenReturn(Optional.of(account));
        when(jwtTokenService.generateAccessToken(any())).thenReturn("new-access-token");
        when(jwtTokenService.generateRefreshToken(any())).thenReturn("new-refresh-token");

        LoginResult result = useCase.execute("refresh-token");

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals(1L, result.userId());
        assertEquals("ADMIN", result.userRole());
    }

    @Test
    void rejectsAccessTokenAsRefreshToken() {
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenUseCase useCase = new RefreshTokenUseCase(jwtDecoder, jwtTokenService, userRepository);

        when(jwtDecoder.decode("access-token")).thenReturn(jwt("ACCESS"));

        assertThrows(BadCredentialsException.class, () -> useCase.execute("access-token"));
    }

    private Jwt jwt(String tokenType) {
        return new Jwt(
                "token",
                Instant.parse("2026-06-13T10:00:00Z"),
                Instant.parse("2026-06-20T10:00:00Z"),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "admin@sindicato.es",
                        "tokenType", tokenType,
                        "roles", List.of("ADMIN")
                )
        );
    }
}
