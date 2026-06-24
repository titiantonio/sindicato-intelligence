package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.user.application.UserAccountNotificationSender;
import es.sindicato.intelligence.user.application.UserNotFoundException;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class ResetPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenHasher passwordResetTokenHasher;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryPolicyService passwordHistoryPolicyService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAuditLogRepository userAuditLogRepository;
    private final UserAccountNotificationSender userAccountNotificationSender;

    public ResetPasswordUseCase(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetTokenHasher passwordResetTokenHasher,
            PasswordEncoder passwordEncoder,
            PasswordHistoryPolicyService passwordHistoryPolicyService,
            RefreshTokenRepository refreshTokenRepository,
            UserAuditLogRepository userAuditLogRepository,
            UserAccountNotificationSender userAccountNotificationSender
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetTokenHasher = passwordResetTokenHasher;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryPolicyService = passwordHistoryPolicyService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAuditLogRepository = userAuditLogRepository;
        this.userAccountNotificationSender = userAccountNotificationSender;
    }

    @Transactional
    public void execute(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token is required");
        }

        passwordHistoryPolicyService.validateComplexity(newPassword);

        OffsetDateTime now = OffsetDateTime.now();
        PasswordResetTokenRecord tokenRecord = passwordResetTokenRepository.findByToken(passwordResetTokenHasher.hash(token))
                .orElseThrow(() -> new IllegalArgumentException("invalid token"));

        if (tokenRecord.isUsed()) {
            throw new IllegalArgumentException("token already used");
        }

        if (tokenRecord.isExpired(now)) {
            throw new IllegalArgumentException("token expired");
        }

        UserAccount user = userRepository.findById(tokenRecord.userId())
                .orElseThrow(() -> new UserNotFoundException(tokenRecord.userId()));

        passwordHistoryPolicyService.validateNotReused(user, newPassword);
        passwordHistoryPolicyService.storeInHistory(user);
        UserAccount updated = userRepository.save(user.withCredentials(passwordEncoder.encode(newPassword), false, null, now));
        passwordResetTokenRepository.markAsUsed(tokenRecord.id(), now);
        refreshTokenRepository.revokeActiveTokensForUser(updated.getId(), now);
        userAuditLogRepository.record(updated.getId(), updated.getEmail(), UserAuditAction.PASSWORD_CHANGED, AuditDetailFormatter.passwordChanged(now));
        userAccountNotificationSender.sendPasswordChangedEmail(updated.getEmail(), updated.getName());
        log.info("password reset completed: userId={}", updated.getId());
    }
}
