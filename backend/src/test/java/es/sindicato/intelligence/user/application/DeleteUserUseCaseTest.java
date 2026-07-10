package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserDeletionDependencies;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteUserUseCaseTest {

    @Test
    void deletesUserWhenNoFunctionalDependenciesExist() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository, userAccountNotificationSender, userAuditLogRepository);
        UserAccount user = new UserAccount(2L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.findDeletionDependencies(2L)).thenReturn(new UserDeletionDependencies(0, 0));

        useCase.execute(2L, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendUserDeletedEmail("editor@sindicato.es", "Editor");
        var ordered = inOrder(userRepository);
        ordered.verify(userRepository).deleteTechnicalDependencies(2L);
        ordered.verify(userRepository).deleteById(2L);
        verify(userAuditLogRepository).record(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.eq("admin@sindicato.es"), org.mockito.ArgumentMatchers.eq(UserAuditAction.USER_DELETED), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsUnknownUsers() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository, userAccountNotificationSender, mock(UserAuditLogRepository.class));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(99L, "admin@sindicato.es"));
    }

    @Test
    void rejectsSelfDeletion() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository, userAccountNotificationSender, mock(UserAuditLogRepository.class));
        UserAccount user = new UserAccount(1L, "admin@sindicato.es", "hash", "Admin", UserRole.ADMIN, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserDeletionConflictException.class, () -> useCase.execute(1L, "admin@sindicato.es"));

        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void rejectsDeletingLastAdmin() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository, userAccountNotificationSender, mock(UserAuditLogRepository.class));
        UserAccount user = new UserAccount(1L, "admin@sindicato.es", "hash", "Admin", UserRole.ADMIN, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.ADMIN)).thenReturn(1L);

        assertThrows(UserDeletionConflictException.class, () -> useCase.execute(1L, "other-admin@sindicato.es"));

        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void rejectsUsersWithFunctionalDependencies() {
        UserRepository userRepository = mock(UserRepository.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        DeleteUserUseCase useCase = new DeleteUserUseCase(userRepository, userAccountNotificationSender, mock(UserAuditLogRepository.class));
        UserAccount user = new UserAccount(2L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.findDeletionDependencies(2L)).thenReturn(new UserDeletionDependencies(1, 2));

        assertThrows(UserDeletionConflictException.class, () -> useCase.execute(2L, "admin@sindicato.es"));

        verify(userRepository, never()).deleteTechnicalDependencies(2L);
        verify(userRepository, never()).deleteById(2L);
        verify(userAccountNotificationSender, never()).sendUserDeletedEmail("editor@sindicato.es", "Editor");
    }
}
