package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class ApproveContentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveContentUseCase.class);

    private final GeneratedContentRepository contentRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public ApproveContentUseCase(GeneratedContentRepository contentRepository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.contentRepository = contentRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public GeneratedContent execute(Long contentId) {
        Objects.requireNonNull(contentId, "contentId is required");

        log.info("content approval started: contentId={}", contentId);
        GeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + contentId));
        OffsetDateTime approvedAt = OffsetDateTime.now();
        content.approve(approvedAt);
        GeneratedContent savedContent = contentRepository.save(content);
        recordAuditLogUseCase.record(
                "CONTENT_APPROVED",
                "CONTENT",
                savedContent.getId(),
                null,
                AuditDetailFormatter.contentApproved(savedContent.getId(), savedContent.getEventId(), approvedAt, savedContent.getStatus().name())
        );
        log.info("content approval completed: contentId={}, eventId={}, status={}", savedContent.getId(), savedContent.getEventId(), savedContent.getStatus());

        return savedContent;
    }
}
