package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestPasswordResetUseCaseTest {

    @Test
    void storesHashedTokenAndSendsRawTokenByEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        PasswordResetTokenHasher passwordResetTokenHasher = mock(PasswordResetTokenHasher.class);
        PasswordResetEmailSender passwordResetEmailSender = mock(PasswordResetEmailSender.class);
        RequestPasswordResetUseCase useCase = new RequestPasswordResetUseCase(
                userRepository,
                passwordResetTokenRepository,
                passwordResetTokenHasher,
                passwordResetEmailSender,
                30
        );
        UserAccount user = new UserAccount(1L, "editor@sindicato.es", "hash", "Editor", UserRole.EDITOR, true, false);

        when(userRepository.findByEmail("editor@sindicato.es")).thenReturn(Optional.of(user));
        when(passwordResetTokenHasher.hash(any())).thenReturn("hashed-reset-token");

        useCase.execute("editor@sindicato.es");

        ArgumentCaptor<String> rawTokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordResetEmailSender).sendPasswordResetEmail(eq("editor@sindicato.es"), rawTokenCaptor.capture());
        verify(passwordResetTokenRepository).create(eq(1L), eq("hashed-reset-token"), any());
        verify(passwordResetTokenRepository).invalidateActiveTokensForUser(eq(1L), any());
        assertTrue(rawTokenCaptor.getValue().length() >= 32);
        assertNotEquals("hashed-reset-token", rawTokenCaptor.getValue());
    }
}
