package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.audit.domain.AuditLogEntry;
import es.sindicato.intelligence.audit.domain.AuditLogQuery;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class ListWorkflowOperationsUseCase {

    private static final ZoneId OPERATIVE_ZONE = ZoneId.of("Europe/Madrid");

    private final AiOperationMetricRepository aiOperationMetricRepository;
    private final PublicationRepository publicationRepository;
    private final GeneratedContentRepository contentRepository;
    private final AuditLogRepository auditLogRepository;

    public ListWorkflowOperationsUseCase(
            AiOperationMetricRepository aiOperationMetricRepository,
            PublicationRepository publicationRepository,
            GeneratedContentRepository contentRepository,
            AuditLogRepository auditLogRepository
    ) {
        this.aiOperationMetricRepository = aiOperationMetricRepository;
        this.publicationRepository = publicationRepository;
        this.contentRepository = contentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkflowOperationView> execute(LocalDate date) {
        LocalDate selectedDate = date == null ? LocalDate.now(OPERATIVE_ZONE) : date;
        OffsetDateTime from = startOfDay(selectedDate);
        OffsetDateTime to = startOfDay(selectedDate.plusDays(1));

        List<WorkflowOperationView> operations = new ArrayList<>();
        operations.addAll(aiOperationMetricRepository.findByCreatedAtBetween(from, to).stream()
                .map(this::toAiOperation)
                .toList());
        operations.addAll(publicationAuditOperations(selectedDate));
        operations.addAll(scheduledPublicationOperations(from, to, operations));

        return operations.stream()
                .sorted(Comparator.comparing(WorkflowOperationView::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    private OffsetDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay(OPERATIVE_ZONE).toOffsetDateTime();
    }

    private WorkflowOperationView toAiOperation(AiOperationMetric metric) {
        return new WorkflowOperationView(
                "AI-" + metric.getId(),
                workflowCode(metric),
                metric.getOperationType(),
                metric.getStatus().name(),
                metric.getRelatedEntityType(),
                metric.getRelatedEntityId(),
                metric.getCreatedAt(),
                metric.getLatencyMs(),
                metric.getPromptKey(),
                metric.getProvider(),
                metric.getModel(),
                metric.getErrorMessage(),
                metric.getOperationDetails()
        );
    }

    private String workflowCode(AiOperationMetric metric) {
        Object detailWorkflowCode = metric.getOperationDetails().get("workflowCode");
        if (detailWorkflowCode instanceof String value && !value.isBlank()) {
            return value;
        }
        return switch (metric.getPromptKey()) {
            case "WF02_CLASSIFICATION" -> "WF02_CLASSIFICATION";
            case "WF03_EVENT_MATCHING" -> "WF03_EVENT_MATCHING";
            case "WF04_ANALYSIS" -> "WF04_ANALYSIS";
            case "WF05_CONTENT" -> "WF05_CONTENT";
            default -> metric.getPromptKey();
        };
    }

    private List<WorkflowOperationView> publicationAuditOperations(LocalDate date) {
        return auditLogRepository.findEditorial(new AuditLogQuery(null, "PUBLICATION", null, date, 500)).stream()
                .filter(entry -> "PUBLICATION_PUBLISHED".equals(entry.action()) || "PUBLICATION_FAILED".equals(entry.action()))
                .map(this::toPublicationOperation)
                .filter(Objects::nonNull)
                .toList();
    }

    private WorkflowOperationView toPublicationOperation(AuditLogEntry audit) {
        Publication publication = publicationRepository.findById(audit.entityId()).orElse(null);
        if (publication == null) {
            return null;
        }
        GeneratedContent content = contentRepository.findById(publication.getContentId()).orElse(null);
        Map<String, Object> details = publicationDetails(publication, content);
        details.put("auditAction", audit.action());
        details.put("auditDetail", audit.newValues());
        details.put("triggerType", publication.getScheduledAt() == null ? "IMMEDIATE" : "SCHEDULED");

        return new WorkflowOperationView(
                "WF06-" + publication.getId() + "-" + audit.id(),
                "WF06_PUBLICATION_TELEGRAM",
                "TELEGRAM_PUBLICATION",
                publication.getStatus() == PublicationStatus.FAILED ? AiMetricStatus.FAILED.name() : AiMetricStatus.SUCCESS.name(),
                "PUBLICATION",
                publication.getId(),
                audit.createdAt(),
                null,
                null,
                "Telegram",
                null,
                publication.getStatus() == PublicationStatus.FAILED ? sanitizeError(publication.getResponsePayload()) : null,
                details
        );
    }

    private List<WorkflowOperationView> scheduledPublicationOperations(
            OffsetDateTime from,
            OffsetDateTime to,
            List<WorkflowOperationView> existingOperations
    ) {
        Set<Long> existingPublicationIds = new HashSet<>();
        existingOperations.stream()
                .filter(operation -> "PUBLICATION".equals(operation.relatedEntityType()))
                .map(WorkflowOperationView::relatedEntityId)
                .filter(Objects::nonNull)
                .forEach(existingPublicationIds::add);

        return publicationRepository.findScheduledBetween(from, to).stream()
                .filter(publication -> !existingPublicationIds.contains(publication.getId()))
                .map(this::toScheduledPublicationOperation)
                .toList();
    }

    private WorkflowOperationView toScheduledPublicationOperation(Publication publication) {
        GeneratedContent content = contentRepository.findById(publication.getContentId()).orElse(null);
        Map<String, Object> details = publicationDetails(publication, content);
        details.put("triggerType", "SCHEDULED");

        return new WorkflowOperationView(
                "WF06-" + publication.getId(),
                "WF06_PUBLICATION_TELEGRAM",
                "TELEGRAM_PUBLICATION",
                publication.getStatus() == PublicationStatus.FAILED ? AiMetricStatus.FAILED.name() : AiMetricStatus.SUCCESS.name(),
                "PUBLICATION",
                publication.getId(),
                publication.getScheduledAt(),
                null,
                null,
                "Telegram",
                null,
                publication.getStatus() == PublicationStatus.FAILED ? sanitizeError(publication.getResponsePayload()) : null,
                details
        );
    }

    private Map<String, Object> publicationDetails(Publication publication, GeneratedContent content) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF06_PUBLICATION_TELEGRAM");
        details.put("publicationId", publication.getId());
        details.put("contentId", publication.getContentId());
        details.put("eventId", content == null ? null : content.getEventId());
        details.put("contentTitle", content == null ? null : abbreviate(content.getTitle()));
        details.put("channel", publication.getChannel());
        details.put("publicationStatus", publication.getStatus().name());
        details.put("externalId", publication.getExternalId());
        details.put("publishedAt", publication.getPublishedAt());
        details.put("scheduledAt", publication.getScheduledAt());
        details.put("error", publication.getStatus() == PublicationStatus.FAILED ? sanitizeError(publication.getResponsePayload()) : null);
        return details;
    }

    private String sanitizeError(String responsePayload) {
        if (responsePayload == null || responsePayload.isBlank()) {
            return null;
        }
        String normalized = responsePayload.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 500) {
            return normalized;
        }
        return normalized.substring(0, 497) + "...";
    }

    private String abbreviate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 180) {
            return normalized;
        }
        return normalized.substring(0, 177) + "...";
    }
}
