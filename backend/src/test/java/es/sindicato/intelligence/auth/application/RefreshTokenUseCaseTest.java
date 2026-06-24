package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenUseCaseTest {

    @Test
    void refreshesAccessAndRefreshTokens() {
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenUseCase useCase = new RefreshTokenUseCase(jwtDecoder, jwtTokenService, refreshTokenRepository, refreshTokenHasher, userRepository);
        UserAccount account = new UserAccount(1L, "admin@sindicato.es", "$2a$10$hash", "Admin Sindicato", UserRole.ADMIN, true, false);
        RefreshTokenRecord tokenRecord = new RefreshTokenRecord(20L, 1L, "token-id", "token-hash", OffsetDateTime.now().minusMinutes(1), OffsetDateTime.now().plusDays(7), null, null);

        when(jwtDecoder.decode("refresh-token")).thenReturn(jwt("REFRESH"));
        when(userRepository.findByEmail("admin@sindicato.es")).thenReturn(Optional.of(account));
        when(refreshTokenRepository.findByTokenId("token-id")).thenReturn(Optional.of(tokenRecord));
        when(refreshTokenHasher.matches("refresh-token", "token-hash")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(any())).thenReturn("new-access-token");
        when(jwtTokenService.issueRefreshToken(any()))
                .thenReturn(new GeneratedRefreshToken("new-refresh-token", "new-token-id", Instant.parse("2026-06-20T10:00:00Z")));
        when(refreshTokenHasher.hash("new-refresh-token")).thenReturn("new-token-hash");

        LoginResult result = useCase.execute("refresh-token");

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals(1L, result.userId());
        assertEquals("ADMIN", result.userRole());
        verify(refreshTokenRepository).markAsReplaced(eq(20L), any());
        verify(refreshTokenRepository).create(eq(1L), eq("new-token-id"), eq("new-token-hash"), any(), any());
    }

    @Test
    void rejectsAccessTokenAsRefreshToken() {
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenUseCase useCase = new RefreshTokenUseCase(jwtDecoder, jwtTokenService, refreshTokenRepository, refreshTokenHasher, userRepository);

        when(jwtDecoder.decode("access-token")).thenReturn(jwt("ACCESS"));

        assertThrows(BadCredentialsException.class, () -> useCase.execute("access-token"));
    }

    @Test
    void rejectsAlreadyReplacedRefreshToken() {
        JwtDecoder jwtDecoder = mock(JwtDecoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenUseCase useCase = new RefreshTokenUseCase(jwtDecoder, jwtTokenService, refreshTokenRepository, refreshTokenHasher, userRepository);
        UserAccount account = new UserAccount(1L, "admin@sindicato.es", "$2a$10$hash", "Admin Sindicato", UserRole.ADMIN, true, false);
        RefreshTokenRecord tokenRecord = new RefreshTokenRecord(20L, 1L, "token-id", "token-hash", OffsetDateTime.now().minusMinutes(1), OffsetDateTime.now().plusDays(7), null, OffsetDateTime.now());

        when(jwtDecoder.decode("refresh-token")).thenReturn(jwt("REFRESH"));
        when(userRepository.findByEmail("admin@sindicato.es")).thenReturn(Optional.of(account));
        when(refreshTokenRepository.findByTokenId("token-id")).thenReturn(Optional.of(tokenRecord));

        assertThrows(BadCredentialsException.class, () -> useCase.execute("refresh-token"));
    }

    private Jwt jwt(String tokenType) {
        return new Jwt(
                "token",
                Instant.parse("2026-06-13T10:00:00Z"),
                Instant.parse("2026-06-20T10:00:00Z"),
                Map.of("alg", "HS256"),
                Map.of(
                        "sub", "admin@sindicato.es",
                        "jti", "token-id",
                        "tokenType", tokenType,
                        "roles", List.of("ADMIN")
                )
        );
    }
}
