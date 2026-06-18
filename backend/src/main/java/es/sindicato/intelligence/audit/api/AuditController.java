package es.sindicato.intelligence.audit.api;

import es.sindicato.intelligence.audit.application.ListEditorialAuditUseCase;
import es.sindicato.intelligence.audit.application.ListUserAuditUseCase;
import es.sindicato.intelligence.audit.domain.AuditLogEntry;
import es.sindicato.intelligence.audit.domain.AuditLogQuery;
import es.sindicato.intelligence.audit.domain.UserAuditLogQuery;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import es.sindicato.intelligence.user.infrastructure.UserAuditLogEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final ListUserAuditUseCase listUserAuditUseCase;
    private final ListEditorialAuditUseCase listEditorialAuditUseCase;
    private final UserRepository userRepository;

    public AuditController(
            ListUserAuditUseCase listUserAuditUseCase,
            ListEditorialAuditUseCase listEditorialAuditUseCase,
            UserRepository userRepository
    ) {
        this.listUserAuditUseCase = listUserAuditUseCase;
        this.listEditorialAuditUseCase = listEditorialAuditUseCase;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<UserAuditLogResponse> listUserAudit(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return listUserAuditUseCase.execute(new UserAuditLogQuery(action, userId, date, limit)).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/editorial")
    public List<AuditLogResponse> listEditorialAudit(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return listEditorialAuditUseCase.execute(new AuditLogQuery(action, entityType, entityId, date, limit)).stream()
                .map(this::toResponse)
                .toList();
    }

    private UserAuditLogResponse toResponse(UserAuditLogEntity entity) {
        return new UserAuditLogResponse(
                entity.getId(),
                entity.getUserId(),
                userDisplayName(entity.getUserId()),
                entity.getActorEmail(),
                entity.getAction(),
                entity.getDetails(),
                entity.getCreatedAt()
        );
    }

    private AuditLogResponse toResponse(AuditLogEntry entry) {
        return new AuditLogResponse(
                entry.id(),
                entry.userId(),
                userDisplayName(entry.userId()),
                entry.action(),
                entry.entityType(),
                entry.entityId(),
                entry.oldValues(),
                entry.newValues(),
                entry.createdAt()
        );
    }

    private String userDisplayName(Long userId) {
        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId)
                .map(this::formatUser)
                .orElse("Usuario #" + userId);
    }

    private String formatUser(UserAccount user) {
        return user.getName() + " <" + user.getEmail() + ">";
    }
}
