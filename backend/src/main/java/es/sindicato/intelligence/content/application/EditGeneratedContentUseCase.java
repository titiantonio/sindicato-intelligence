package es.sindicato.intelligence.content.application;

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

        String oldValues = "{\"title\":\"" + escapeJson(content.getTitle()) + "\",\"tone\":\""
                + escapeJson(content.getTone()) + "\",\"status\":\"" + content.getStatus() + "\"}";

        content.edit(command.title(), command.content(), command.tone());
        GeneratedContent savedContent = contentRepository.save(content);

        String newValues = "{\"title\":\"" + escapeJson(savedContent.getTitle()) + "\",\"tone\":\""
                + escapeJson(savedContent.getTone()) + "\",\"status\":\"" + savedContent.getStatus() + "\"}";
        recordAuditLogUseCase.record("CONTENT_EDITED", "CONTENT", savedContent.getId(), oldValues, newValues);

        return savedContent;
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
