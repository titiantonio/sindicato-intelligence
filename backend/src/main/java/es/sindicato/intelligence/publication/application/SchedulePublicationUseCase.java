package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class SchedulePublicationUseCase {

    private final GeneratedContentRepository contentRepository;
    private final PublicationRepository publicationRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public SchedulePublicationUseCase(
            GeneratedContentRepository contentRepository,
            PublicationRepository publicationRepository,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.contentRepository = contentRepository;
        this.publicationRepository = publicationRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public Publication execute(SchedulePublicationCommand command) {
        Objects.requireNonNull(command.contentId(), "contentId is required");
        Objects.requireNonNull(command.scheduledAt(), "scheduledAt is required");

        if (!command.scheduledAt().isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("scheduledAt must be in the future");
        }

        GeneratedContent content = contentRepository.findById(command.contentId())
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + command.contentId()));
        if (content.getStatus() != ContentStatus.APPROVED) {
            throw new IllegalStateException("only approved content can be scheduled");
        }

        Publication publication = publicationRepository.save(Publication.scheduled(
                content.getId(),
                content.getChannel(),
                command.scheduledAt()
        ));

        recordAuditLogUseCase.record(
                "PUBLICATION_SCHEDULED",
                "PUBLICATION",
                publication.getId(),
                null,
                AuditDetailFormatter.publicationScheduled(
                        publication.getId(),
                        content.getId(),
                        content.getEventId(),
                        publication.getChannel(),
                        publication.getScheduledAt(),
                        publication.getStatus().name()
                )
        );

        return publication;
    }
}
