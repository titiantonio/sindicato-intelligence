package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.application.UserAccountNotificationSender;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangePasswordUseCaseTest {

    @Test
    void sendsPasswordChangedNotificationAfterSuccessfulChange() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        PasswordHistoryPolicyService passwordHistoryPolicyService = mock(PasswordHistoryPolicyService.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        ChangePasswordUseCase useCase = new ChangePasswordUseCase(
                userRepository,
                passwordEncoder,
                passwordHistoryPolicyService,
                refreshTokenRepository,
                userAuditLogRepository,
                userAccountNotificationSender
        );
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "old-hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findByEmail("editor@sindicato.es")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Temporal#123", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("Nueva#12345")).thenReturn("new-hash");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute("editor@sindicato.es", "Temporal#123", "Nueva#12345");

        verify(userAccountNotificationSender).sendPasswordChangedEmail("editor@sindicato.es", "Editor");
        verify(refreshTokenRepository).revokeActiveTokensForUser(eq(1L), any());
    }
}
