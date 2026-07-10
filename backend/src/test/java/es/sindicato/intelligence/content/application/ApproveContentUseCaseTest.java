package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApproveContentUseCaseTest {

    @Test
    void recordsAuditWhenContentIsApproved() {
        GeneratedContentRepository repository = mock(GeneratedContentRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        ApproveContentUseCase useCase = new ApproveContentUseCase(repository, audit);
        GeneratedContent content = content(ContentStatus.PENDING_REVIEW);

        when(repository.findById(10L)).thenReturn(Optional.of(content));
        when(repository.save(content)).thenReturn(content);

        GeneratedContent result = useCase.execute(10L);

        assertEquals(ContentStatus.APPROVED, result.getStatus());
        verify(audit).record(eq("CONTENT_APPROVED"), eq("CONTENT"), eq(10L), eq(null), any());
    }

    private GeneratedContent content(ContentStatus status) {
        return new GeneratedContent(10L, 20L, 30L, 1L, "TELEGRAM", "INFORMATIVO", "Titulo", "Texto", status, OffsetDateTime.parse("2026-06-08T10:00:00Z"), null);
    }
}
