package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.auth.infrastructure.UserSecurityDetails;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

    @Test
    void authenticatesAndReturnsTokens() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        LoginUseCase useCase = new LoginUseCase(authenticationManager, jwtTokenService, userRepository, userAuditLogRepository);
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
        when(jwtTokenService.generateRefreshToken(org.mockito.ArgumentMatchers.any())).thenReturn("refresh-token");

        LoginResult result = useCase.execute(new LoginCommand("admin@sindicato.es", "secret"));

        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(1L, result.userId());
        assertEquals("Admin Sindicato", result.userName());
        assertEquals("ADMIN", result.userRole());
        assertEquals(false, result.mustChangePassword());
    }

    @Test
    void failsWhenCredentialsAreInvalid() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        LoginUseCase useCase = new LoginUseCase(authenticationManager, jwtTokenService, userRepository, userAuditLogRepository);

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> useCase.execute(new LoginCommand("admin@sindicato.es", "wrong")));
    }
}
