package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RequestPasswordResetUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetUseCase.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetEmailSender passwordResetEmailSender;
    private final int resetTokenMinutes;

    public RequestPasswordResetUseCase(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetEmailSender passwordResetEmailSender,
            @Value("${app.security.password-reset.token-minutes:30}") int resetTokenMinutes
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetEmailSender = passwordResetEmailSender;
        this.resetTokenMinutes = resetTokenMinutes;
    }

    @Transactional
    public void execute(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        UserAccount user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.canAuthenticate()) {
            log.info("password reset requested for unknown or unavailable email");
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        String token = UUID.randomUUID().toString();
        OffsetDateTime expiresAt = now.plusMinutes(resetTokenMinutes);

        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId(), now);
        passwordResetTokenRepository.create(user.getId(), token, expiresAt);
        passwordResetEmailSender.sendPasswordResetEmail(user.getEmail(), token);

        log.info("password reset token created: userId={}, expiresAt={}", user.getId(), expiresAt);
    }
}
