package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UpdateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserUseCase.class);

    private final UserRepository userRepository;
    private final UserAuditLogRepository userAuditLogRepository;
    private final UserAccountNotificationSender userAccountNotificationSender;

    public UpdateUserUseCase(
            UserRepository userRepository,
            UserAuditLogRepository userAuditLogRepository,
            UserAccountNotificationSender userAccountNotificationSender
    ) {
        this.userRepository = userRepository;
        this.userAuditLogRepository = userAuditLogRepository;
        this.userAccountNotificationSender = userAccountNotificationSender;
    }

    @Transactional
    public UserAccount execute(Long userId, UpdateUserCommand command) {
        return execute(userId, command, "system");
    }

    @Transactional
    public UserAccount execute(Long userId, UpdateUserCommand command, String actorEmail) {
        Objects.requireNonNull(command, "command is required");

        UserAccount existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserAccount updated = userRepository.save(existing.withProfile(command.name(), command.role()));
        if (existing.getRole() != updated.getRole()) {
            userAuditLogRepository.record(userId, actorEmail, UserAuditAction.USER_ROLE_CHANGED,
                    AuditDetailFormatter.userRoleChanged(existing.getRole(), updated.getRole()));
        }
        if (!existing.getName().equals(updated.getName()) || existing.getRole() != updated.getRole()) {
            userAccountNotificationSender.sendUserUpdatedEmail(updated.getEmail(), updated.getName());
        }
        log.info("user update completed: userId={}, role={}", updated.getId(), updated.getRole());

        return updated;
    }
}
