package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserPasswordHistoryRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class ResetTemporaryPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResetTemporaryPasswordUseCase.class);

    private final UserRepository userRepository;
    private final UserPasswordHistoryRepository userPasswordHistoryRepository;
    private final UserAuditLogRepository userAuditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final UserAccountNotificationSender userAccountNotificationSender;
    private final int temporaryPasswordDays;

    public ResetTemporaryPasswordUseCase(
            UserRepository userRepository,
            UserPasswordHistoryRepository userPasswordHistoryRepository,
            UserAuditLogRepository userAuditLogRepository,
            PasswordEncoder passwordEncoder,
            TemporaryPasswordGenerator temporaryPasswordGenerator,
            UserAccountNotificationSender userAccountNotificationSender,
            @Value("${app.security.temporary-password.expiration-days:7}") int temporaryPasswordDays
    ) {
        this.userRepository = userRepository;
        this.userPasswordHistoryRepository = userPasswordHistoryRepository;
        this.userAuditLogRepository = userAuditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.temporaryPasswordGenerator = temporaryPasswordGenerator;
        this.userAccountNotificationSender = userAccountNotificationSender;
        this.temporaryPasswordDays = temporaryPasswordDays;
    }

    @Transactional
    public UserAccount execute(Long userId, String actorEmail) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return reset(user, actorEmail);
    }

    @Transactional
    public void executeForEmailIfEligible(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        UserAccount user = userRepository.findByEmail(email).orElse(null);
        OffsetDateTime now = OffsetDateTime.now();
        if (user == null || !user.canAuthenticate() || !user.isTemporaryPasswordExpired(now)) {
            log.info("temporary password request ignored for unknown, unavailable or non-expired account");
            return;
        }

        reset(user, "self-service");
    }

    private UserAccount reset(UserAccount user, String actorEmail) {
        String temporaryPassword = temporaryPasswordGenerator.generate();
        String passwordHash = passwordEncoder.encode(temporaryPassword);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusDays(temporaryPasswordDays);

        userPasswordHistoryRepository.save(user.getId(), user.getPasswordHash());
        UserAccount updated = userRepository.save(user.withCredentials(passwordHash, true, expiresAt, user.getLastPasswordChangeAt()));
        userPasswordHistoryRepository.save(updated.getId(), passwordHash);
        userAccountNotificationSender.sendTemporaryPasswordEmail(updated.getEmail(), updated.getName(), temporaryPassword);
        userAuditLogRepository.record(updated.getId(), actorEmail, UserAuditAction.TEMPORARY_PASSWORD_RESET,
                "temporaryPasswordExpiresAt=" + expiresAt);

        log.info("temporary password reset completed: userId={}, expiresAt={}", updated.getId(), expiresAt);
        return updated;
    }
}

