package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateUserUseCaseTest {

    @Test
    void sendsNotificationWhenUserProfileChanges() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        UpdateUserUseCase useCase = new UpdateUserUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, new UpdateUserCommand("Editor Senior", UserRole.ADMIN), "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserUpdatedEmail("editor@sindicato.es", "Editor Senior");
    }

    @Test
    void doesNotSendNotificationWhenNoUserDataChanges() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        UpdateUserUseCase useCase = new UpdateUserUseCase(userRepository, userAuditLogRepository, userAccountNotificationSender);
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, new UpdateUserCommand("Editor", UserRole.EDITOR), "admin@sindicato.es");

        verify(userAccountNotificationSender, never()).sendUserUpdatedEmail(any(), any());
    }
}
