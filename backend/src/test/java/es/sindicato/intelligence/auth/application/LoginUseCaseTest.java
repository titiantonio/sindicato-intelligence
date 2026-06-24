package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.auth.infrastructure.UserSecurityDetails;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

    @Test
    void authenticatesAndReturnsTokens() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        LoginUseCase useCase = new LoginUseCase(authenticationManager, jwtTokenService, refreshTokenRepository, refreshTokenHasher, userRepository, userAuditLogRepository);
        UserAccount account = new UserAccount(
                1L,
                "admin@sindicato.es",
                "$2a$10$hash",
                "Admin Sindicato",
                UserRole.ADMIN,
                true,
                false
        );
        UserSecurityDetails principal = new UserSecurityDetails(account);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any())).thenReturn(authentication);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(account));
        when(userRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenService.generateAccessToken(org.mockito.ArgumentMatchers.any())).thenReturn("access-token");
        when(jwtTokenService.issueRefreshToken(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new GeneratedRefreshToken("refresh-token", "token-id", Instant.parse("2026-06-17T10:00:00Z")));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("token-hash");

        LoginResult result = useCase.execute(new LoginCommand("admin@sindicato.es", "secret"));

        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(1L, result.userId());
        assertEquals("Admin Sindicato", result.userName());
        assertEquals("ADMIN", result.userRole());
        assertEquals(false, result.mustChangePassword());
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(userAuditLogRepository).record(eq(1L), eq("admin@sindicato.es"), eq(UserAuditAction.LOGIN), detailCaptor.capture());
        verify(refreshTokenRepository).create(eq(1L), eq("token-id"), eq("token-hash"), any(), any());
        assertTrue(detailCaptor.getValue().contains("Login completado correctamente"));
    }

    @Test
    void failsWhenCredentialsAreInvalid() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        LoginUseCase useCase = new LoginUseCase(authenticationManager, jwtTokenService, refreshTokenRepository, refreshTokenHasher, userRepository, userAuditLogRepository);

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> useCase.execute(new LoginCommand("admin@sindicato.es", "wrong")));
    }
}
