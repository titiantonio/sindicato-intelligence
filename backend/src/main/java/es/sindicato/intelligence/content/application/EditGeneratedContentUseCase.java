package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditGeneratedContentUseCase {

    private final GeneratedContentRepository contentRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public EditGeneratedContentUseCase(
            GeneratedContentRepository contentRepository,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.contentRepository = contentRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public GeneratedContent execute(EditGeneratedContentCommand command) {
        GeneratedContent content = contentRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + command.id()));

        String oldValues = AuditDetailFormatter.contentEditedBefore(
                content.getId(),
                content.getEventId(),
                content.getTitle(),
                content.getTone(),
                content.getStatus().name()
        );

        content.edit(command.title(), command.content(), command.tone());
        GeneratedContent savedContent = contentRepository.save(content);

        String newValues = AuditDetailFormatter.contentEditedAfter(
                savedContent.getId(),
                savedContent.getEventId(),
                savedContent.getTitle(),
                savedContent.getTone(),
                savedContent.getStatus().name()
        );
        recordAuditLogUseCase.record("CONTENT_EDITED", "CONTENT", savedContent.getId(), oldValues, newValues);

        return savedContent;
    }
}
