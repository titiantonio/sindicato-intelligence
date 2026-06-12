package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeUserStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangeUserStatusUseCase.class);

    private final UserRepository userRepository;
    private final UserAuditLogRepository userAuditLogRepository;
    private final UserAccountNotificationSender userAccountNotificationSender;

    public ChangeUserStatusUseCase(
            UserRepository userRepository,
            UserAuditLogRepository userAuditLogRepository,
            UserAccountNotificationSender userAccountNotificationSender
    ) {
        this.userRepository = userRepository;
        this.userAuditLogRepository = userAuditLogRepository;
        this.userAccountNotificationSender = userAccountNotificationSender;
    }

    @Transactional
    public UserAccount execute(Long userId, UserStatus status, String actorEmail) {
        UserAccount existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserAccount updated = userRepository.save(existing.withStatus(status));
        userAuditLogRepository.record(userId, actorEmail, actionFor(status), "status=" + status);
        sendNotificationIfRequired(updated, status);
        log.info("user status changed: userId={}, status={}", userId, status);
        return updated;
    }

    private void sendNotificationIfRequired(UserAccount user, UserStatus status) {
        if (status == UserStatus.LOCKED) {
            userAccountNotificationSender.sendUserBlockedEmail(user.getEmail(), user.getName());
        }
        if (status == UserStatus.INACTIVE) {
            userAccountNotificationSender.sendUserDeactivatedEmail(user.getEmail(), user.getName());
        }
    }

    private UserAuditAction actionFor(UserStatus status) {
        return switch (status) {
            case ACTIVE -> UserAuditAction.USER_ACTIVATED;
            case INACTIVE -> UserAuditAction.USER_DEACTIVATED;
            case LOCKED -> UserAuditAction.USER_LOCKED;
            case PENDING_ACTIVATION -> UserAuditAction.USER_UNLOCKED;
        };
    }
}
