package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserDeletionDependencies;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteUserUseCase.class);

    private final UserRepository userRepository;
    private final UserAccountNotificationSender userAccountNotificationSender;
    private final UserAuditLogRepository userAuditLogRepository;

    public DeleteUserUseCase(
            UserRepository userRepository,
            UserAccountNotificationSender userAccountNotificationSender,
            UserAuditLogRepository userAuditLogRepository
    ) {
        this.userRepository = userRepository;
        this.userAccountNotificationSender = userAccountNotificationSender;
        this.userAuditLogRepository = userAuditLogRepository;
    }

    @Transactional
    public void execute(Long userId, String actorEmail) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmail().equalsIgnoreCase(actorEmail)) {
            log.warn("user deletion rejected because actor attempted self-delete: userId={}", userId);
            throw new UserDeletionConflictException("No se puede eliminar el usuario autenticado.");
        }

        if (user.getRole() == UserRole.ADMIN && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            log.warn("user deletion rejected because target is the last admin: userId={}", userId);
            throw new UserDeletionConflictException("No se puede eliminar el ultimo usuario ADMIN.");
        }

        UserDeletionDependencies dependencies = userRepository.findDeletionDependencies(userId);
        if (dependencies.hasFunctionalDependencies()) {
            log.warn(
                    "user deletion rejected because functional dependencies exist: userId={}, dependencies={}",
                    userId,
                    dependencies.describeFunctionalDependencies()
            );
            throw new UserDeletionConflictException(
                    "No se puede eliminar el usuario porque conserva referencias funcionales: "
                            + dependencies.describeFunctionalDependencies()
            );
        }

        userAccountNotificationSender.sendUserDeletedEmail(user.getEmail(), user.getName());
        userRepository.deleteTechnicalDependencies(userId);
        userRepository.deleteById(userId);
        userAuditLogRepository.record(null, actorEmail, UserAuditAction.USER_DELETED, AuditDetailFormatter.userDeleted(userId, user.getEmail(), user.getRole()));
        log.info("user deletion completed: userId={}", userId);
    }
}
