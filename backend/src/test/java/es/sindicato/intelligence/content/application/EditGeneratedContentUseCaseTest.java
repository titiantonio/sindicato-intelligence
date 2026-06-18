package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditGeneratedContentUseCaseTest {

    @Test
    void editsContentAndRecordsAudit() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        EditGeneratedContentUseCase useCase = new EditGeneratedContentUseCase(contentRepository, audit);
        GeneratedContent content = content(ContentStatus.APPROVED);

        when(contentRepository.findById(10L)).thenReturn(Optional.of(content));
        when(contentRepository.save(content)).thenReturn(content);

        GeneratedContent result = useCase.execute(new EditGeneratedContentCommand(10L, "Nuevo", "Mensaje", "URGENTE"));

        assertEquals(ContentStatus.PENDING_REVIEW, result.getStatus());
        assertEquals("Nuevo", result.getTitle());
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(audit).record(eq("CONTENT_EDITED"), eq("CONTENT"), eq(10L), any(), detailCaptor.capture());
        assertTrue(detailCaptor.getValue().contains("Contenido #10"));
        assertTrue(detailCaptor.getValue().contains("evento #20"));
        assertTrue(detailCaptor.getValue().contains("Estado resultante: PENDING_REVIEW"));
        assertFalse(detailCaptor.getValue().startsWith("{"));
    }

    private GeneratedContent content(ContentStatus status) {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-10T10:00:00Z");
        return new GeneratedContent(10L, 20L, 1L, "TELEGRAM", "INFO", "Titulo", "Contenido", status, generatedAt, generatedAt.plusHours(1));
    }
}
