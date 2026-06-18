package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchedulePublicationUseCaseTest {

    @Test
    void schedulesApprovedContentAndRecordsAudit() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        SchedulePublicationUseCase useCase = new SchedulePublicationUseCase(contentRepository, publicationRepository, audit);
        GeneratedContent content = content(ContentStatus.APPROVED);
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusDays(1);

        when(contentRepository.findById(10L)).thenReturn(Optional.of(content));
        when(publicationRepository.save(any(Publication.class))).thenAnswer(invocation -> {
            Publication publication = invocation.getArgument(0);
            return new Publication(70L, publication.getContentId(), publication.getChannel(), null, publication.getStatus(), null, null, publication.getScheduledAt());
        });

        Publication result = useCase.execute(new SchedulePublicationCommand(10L, scheduledAt));

        assertEquals(PublicationStatus.SCHEDULED, result.getStatus());
        assertEquals(scheduledAt, result.getScheduledAt());
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(audit).record(eq("PUBLICATION_SCHEDULED"), eq("PUBLICATION"), eq(70L), eq(null), detailCaptor.capture());
        assertTrue(detailCaptor.getValue().contains("Publicacion #70 programada"));
        assertTrue(detailCaptor.getValue().contains("contenido #10"));
        assertTrue(detailCaptor.getValue().contains("evento #20"));
        assertFalse(detailCaptor.getValue().startsWith("{"));
    }

    @Test
    void rejectsPastSchedule() {
        SchedulePublicationUseCase useCase = new SchedulePublicationUseCase(
                mock(GeneratedContentRepository.class),
                mock(PublicationRepository.class),
                mock(RecordAuditLogUseCase.class)
        );

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new SchedulePublicationCommand(10L, OffsetDateTime.now().minusMinutes(1))));
    }

    private GeneratedContent content(ContentStatus status) {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-10T10:00:00Z");
        return new GeneratedContent(10L, 20L, 1L, "TELEGRAM", "INFO", "Titulo", "Contenido", status, generatedAt, generatedAt.plusHours(1));
    }
}
