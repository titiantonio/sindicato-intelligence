package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserPasswordHistoryRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CreateUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateUserUseCase.class);

    private final UserRepository userRepository;
    private final UserPasswordHistoryRepository userPasswordHistoryRepository;
    private final UserAuditLogRepository userAuditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;
    private final UserAccountNotificationSender userAccountNotificationSender;
    private final int temporaryPasswordDays;

    public CreateUserUseCase(
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
    public UserAccount execute(CreateUserCommand command) {
        return execute(command, "system");
    }

    @Transactional
    public UserAccount execute(CreateUserCommand command, String actorEmail) {
        Objects.requireNonNull(command, "command is required");

        if (userRepository.existsByEmail(command.email())) {
            log.warn("user creation skipped because email already exists: email={}", command.email());
            throw new IllegalArgumentException("user email already exists");
        }

        String temporaryPassword = temporaryPasswordGenerator.generate();
        String passwordHash = passwordEncoder.encode(temporaryPassword);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusDays(temporaryPasswordDays);

        UserAccount created = userRepository.save(new UserAccount(
                null,
                command.email(),
                passwordHash,
                command.name(),
                command.role(),
                true,
                true,
                UserStatus.PENDING_ACTIVATION,
                expiresAt,
                null,
                null
        ));

        userPasswordHistoryRepository.save(created.getId(), passwordHash);
        userAccountNotificationSender.sendTemporaryPasswordEmail(created.getEmail(), created.getName(), temporaryPassword);
        userAuditLogRepository.record(created.getId(), actorEmail, UserAuditAction.USER_CREATED, AuditDetailFormatter.userCreated(created.getRole()));

        log.info(
                "user creation completed with temporary credentials: userId={}, role={}, temporaryPasswordExpiresAt={}",
                created.getId(),
                created.getRole(),
                expiresAt
        );
        return created;
    }
}

