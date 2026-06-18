package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PublishScheduledPublicationsUseCase {

    private static final Logger log = LoggerFactory.getLogger(PublishScheduledPublicationsUseCase.class);

    private final GeneratedContentRepository contentRepository;
    private final PublicationRepository publicationRepository;
    private final List<PublishingProvider> publishingProviders;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public PublishScheduledPublicationsUseCase(
            GeneratedContentRepository contentRepository,
            PublicationRepository publicationRepository,
            List<PublishingProvider> publishingProviders,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.contentRepository = contentRepository;
        this.publicationRepository = publicationRepository;
        this.publishingProviders = publishingProviders;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public int execute(OffsetDateTime now, int limit) {
        List<Publication> duePublications = publicationRepository.findDueScheduled(now, limit);
        duePublications.forEach(this::publishDuePublication);
        return duePublications.size();
    }

    private void publishDuePublication(Publication publication) {
        log.info("scheduled publication started: publicationId={}, contentId={}, channel={}",
                publication.getId(), publication.getContentId(), publication.getChannel());

        GeneratedContent content = null;
        try {
            content = contentRepository.findById(publication.getContentId())
                    .orElseThrow(() -> new IllegalStateException("scheduled publication content not found: " + publication.getContentId()));
            PublishingProvider publishingProvider = resolveProvider(publication.getChannel());

            PublishingResult result = publishingProvider.publish(new PublishingRequest(
                    content.getId(),
                    content.getChannel(),
                    content.getTitle(),
                    content.getContent()
            ));

            publication.markPublished(result.externalId(), OffsetDateTime.now(), result.responsePayload());
            publicationRepository.save(publication);
            content.markPublished();
            contentRepository.save(content);
            recordPublishedAudit(publication, content);

            log.info("scheduled publication completed: publicationId={}, contentId={}, channel={}",
                    publication.getId(), publication.getContentId(), publication.getChannel());
        } catch (PublishingProviderException exception) {
            markFailed(publication, publication.getContentId(), eventId(content), exception.getMessage(), exception);
        } catch (IllegalStateException exception) {
            markFailed(publication, publication.getContentId(), eventId(content), exception.getMessage(), exception);
        }
    }

    private PublishingProvider resolveProvider(String channel) {
        return publishingProviders.stream()
                .filter(provider -> provider.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("publication provider not found for channel: " + channel));
    }

    private void markFailed(Publication publication, Long contentId, Long eventId, String reason, RuntimeException exception) {
        publication.markFailed(errorPayload(reason));
        publicationRepository.save(publication);
        recordFailedAudit(publication, contentId, eventId, reason);
        log.error("scheduled publication failed: publicationId={}, contentId={}, channel={}, reason={}",
                publication.getId(), publication.getContentId(), publication.getChannel(), reason, exception);
    }

    private String errorPayload(String message) {
        return "{\"ok\":false,\"description\":\"" + escapeJson(truncate(message)) + "\"}";
    }

    private String truncate(String value) {
        if (value == null) {
            return "publication failed";
        }
        if (value.length() <= 500) {
            return value;
        }

        return value.substring(0, 500);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private void recordPublishedAudit(Publication publication, GeneratedContent content) {
        recordAuditLogUseCase.record(
                "PUBLICATION_PUBLISHED",
                "PUBLICATION",
                publication.getId(),
                null,
                AuditDetailFormatter.publicationPublished(
                        publication.getId(),
                        content.getId(),
                        content.getEventId(),
                        publication.getChannel(),
                        publication.getStatus().name(),
                        publication.getExternalId(),
                        true
                )
        );
    }

    private void recordFailedAudit(Publication publication, Long contentId, Long eventId, String reason) {
        recordAuditLogUseCase.record(
                "PUBLICATION_FAILED",
                "PUBLICATION",
                publication.getId(),
                null,
                AuditDetailFormatter.publicationFailed(
                        publication.getId(),
                        contentId,
                        eventId,
                        publication.getChannel(),
                        publication.getStatus().name(),
                        reason,
                        true
                )
        );
    }

    private Long eventId(GeneratedContent content) {
        return content == null ? null : content.getEventId();
    }
}
