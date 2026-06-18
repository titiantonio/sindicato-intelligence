package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserPasswordHistoryRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResetTemporaryPasswordUseCaseTest {

    @Test
    void sendsTemporaryPasswordNotificationWhenResetIsGenerated() {
        UserRepository userRepository = mock(UserRepository.class);
        UserPasswordHistoryRepository userPasswordHistoryRepository = mock(UserPasswordHistoryRepository.class);
        UserAuditLogRepository userAuditLogRepository = mock(UserAuditLogRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        TemporaryPasswordGenerator temporaryPasswordGenerator = mock(TemporaryPasswordGenerator.class);
        UserAccountNotificationSender userAccountNotificationSender = mock(UserAccountNotificationSender.class);
        ResetTemporaryPasswordUseCase useCase = new ResetTemporaryPasswordUseCase(
                userRepository,
                userPasswordHistoryRepository,
                userAuditLogRepository,
                passwordEncoder,
                temporaryPasswordGenerator,
                userAccountNotificationSender,
                7
        );
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "old-hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(temporaryPasswordGenerator.generate()).thenReturn("Temporal#12345");
        when(passwordEncoder.encode("Temporal#12345")).thenReturn("new-hash");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(1L, "admin@sindicato.es");

        verify(userAccountNotificationSender).sendTemporaryPasswordEmail("editor@sindicato.es", "Editor", "Temporal#12345");
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(userAuditLogRepository).record(eq(1L), eq("admin@sindicato.es"), eq(UserAuditAction.TEMPORARY_PASSWORD_RESET), detailCaptor.capture());
        assertTrue(detailCaptor.getValue().contains("Password temporal regenerada"));
    }
}
