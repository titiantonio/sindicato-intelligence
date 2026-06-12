package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.LOCKED, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserBlockedEmail("editor@sindicato.es", "Editor");
        verify(userAccountNotificationSender, never()).sendUserDeactivatedEmail(any(), any());
    }

    @Test
    void sendsDeactivatedNotificationWhenUserIsInactive() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        ChangeUserStatusUseCase useCase = new ChangeUserStatusUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, UserStatus.INACTIVE, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserDeactivatedEmail("editor@sindicato.es", "Editor");
        verify(userAccountNotificationSender, never()).sendUserBlockedEmail(any(), any());
    }
}
