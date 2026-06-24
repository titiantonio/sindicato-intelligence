package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.auth.application.RefreshTokenRepository;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChangeUserStatusUseCaseTest {

    @Test
    void sendsBlockedNotificationWhenUserIsLocked() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender, refreshTokenRepository);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.LOCKED, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserBlockedEmail("editor@sindicato.es", "Editor");
        verify(refreshTokenRepository).revokeActiveTokensForUser(eq(1L), any());
        verify(userAccountNotificationSender, never()).sendUserDeactivatedEmail(any(), any());
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(userAuditLogRepository).record(eq(1L), eq("admin@sindicato.es"), eq(UserAuditAction.USER_LOCKED), detailCaptor.capture());
        assertTrue(detailCaptor.getValue().contains("Estado de usuario actualizado"));
    }

    @Test
    void sendsDeactivatedNotificationWhenUserIsInactive() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender, refreshTokenRepository);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.INACTIVE, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserDeactivatedEmail("editor@sindicato.es", "Editor");
        verify(refreshTokenRepository).revokeActiveTokensForUser(eq(1L), any());
        verify(userAccountNotificationSender, never()).sendUserBlockedEmail(any(), any());
    }

    @Test
    void sendsActivatedNotificationWhenUserIsActivated() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender, refreshTokenRepository);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, false, false,
                UserStatus.INACTIVE, null, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.ACTIVE, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserActivatedEmail("editor@sindicato.es", "Editor");
        verify(userAccountNotificationSender, never()).sendUserUnlockedEmail(any(), any());
    }

    @Test
    void sendsUnlockedNotificationWhenLockedUserIsActivated() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender, refreshTokenRepository);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, false, false,
                UserStatus.LOCKED, null, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.ACTIVE, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserUnlockedEmail("editor@sindicato.es", "Editor");
        verify(userAccountNotificationSender, never()).sendUserActivatedEmail(any(), any());
    }
}
