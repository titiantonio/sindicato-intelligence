package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.application.UserAccountNotificationSender;
import es.sindicato.intelligence.user.application.UserNotFoundException;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class ChangePasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryPolicyService passwordHistoryPolicyService;
    private final UserAuditLogRepository userAuditLogRepository;
    private final UserAccountNotificationSender userAccountNotificationSender;

    public ChangePasswordUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PasswordHistoryPolicyService passwordHistoryPolicyService,
            UserAuditLogRepository userAuditLogRepository,
            UserAccountNotificationSender userAccountNotificationSender
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryPolicyService = passwordHistoryPolicyService;
        this.userAuditLogRepository = userAuditLogRepository;
        this.userAccountNotificationSender = userAccountNotificationSender;
    }

    @Transactional
    public void execute(String email, String currentPassword, String newPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("currentPassword is required");
        }

        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        OffsetDateTime now = OffsetDateTime.now();

        if (user.isTemporaryPasswordExpired(now)) {
            throw new CredentialsExpiredException("temporary password expired");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("invalid current password");
        }

        passwordHistoryPolicyService.validateComplexity(newPassword);
        passwordHistoryPolicyService.validateNotReused(user, newPassword);
        passwordHistoryPolicyService.storeInHistory(user);

        String encoded = passwordEncoder.encode(newPassword);
        UserAccount updated = userRepository.save(user.withCredentials(encoded, false, null, now));
        userAuditLogRepository.record(updated.getId(), updated.getEmail(), UserAuditAction.PASSWORD_CHANGED, "passwordChangedAt=" + now);
        userAccountNotificationSender.sendPasswordChangedEmail(updated.getEmail(), updated.getName());

        log.info("password changed by authenticated user: userId={}", user.getId());
    }
}
