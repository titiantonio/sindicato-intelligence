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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PublishContentUseCaseTest {

    @Test
    void publishesApprovedContentAndMarksContentAsPublished() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        PublishingProvider publishingProvider = mock(PublishingProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishContentUseCase useCase = new PublishContentUseCase(contentRepository, publicationRepository, List.of(publishingProvider), audit);
        GeneratedContent content = approvedContent();

        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(publicationRepository.save(any(Publication.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));
        when(publishingProvider.supports("TELEGRAM")).thenReturn(true);
        when(publishingProvider.publish(any(PublishingRequest.class))).thenReturn(new PublishingResult("message-123", "{\"ok\":true}"));

        Publication result = useCase.execute(content.getId());

        assertEquals(PublicationStatus.PUBLISHED, result.getStatus());
        assertEquals("message-123", result.getExternalId());
        assertEquals(ContentStatus.PUBLISHED, content.getStatus());
        verify(contentRepository).save(content);
        verify(publicationRepository, times(2)).save(any(Publication.class));
        verify(audit).record(eq("PUBLICATION_PUBLISHED"), eq("PUBLICATION"), eq(50L), eq(null), any());
    }

    @Test
    void rejectsContentThatIsNotApproved() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        PublishingProvider publishingProvider = mock(PublishingProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishContentUseCase useCase = new PublishContentUseCase(contentRepository, publicationRepository, List.of(publishingProvider), audit);
        GeneratedContent content = pendingReviewContent();

        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));

        assertThrows(IllegalStateException.class, () -> useCase.execute(content.getId()));

        verify(publicationRepository, never()).save(any(Publication.class));
        verify(publishingProvider, never()).publish(any(PublishingRequest.class));
    }

    @Test
    void registersFailedPublicationWhenProviderFails() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        PublishingProvider publishingProvider = mock(PublishingProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishContentUseCase useCase = new PublishContentUseCase(contentRepository, publicationRepository, List.of(publishingProvider), audit);
        GeneratedContent content = approvedContent();

        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(publicationRepository.save(any(Publication.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));
        when(publishingProvider.supports("TELEGRAM")).thenReturn(true);
        when(publishingProvider.publish(any(PublishingRequest.class))).thenThrow(new PublishingProviderException("provider unavailable"));

        assertThrows(PublishingProviderException.class, () -> useCase.execute(content.getId()));

        ArgumentCaptor<Publication> publicationCaptor = ArgumentCaptor.forClass(Publication.class);
        verify(publicationRepository, times(2)).save(publicationCaptor.capture());
        assertEquals(PublicationStatus.FAILED, publicationCaptor.getAllValues().getLast().getStatus());
        verify(contentRepository, never()).save(content);
        verify(audit).record(eq("PUBLICATION_FAILED"), eq("PUBLICATION"), eq(50L), eq(null), any());
    }

    private Publication withId(Publication publication) {
        if (publication.getId() != null) {
            return publication;
        }

        return new Publication(
                50L,
                publication.getContentId(),
                publication.getChannel(),
                publication.getExternalId(),
                publication.getStatus(),
                publication.getPublishedAt(),
                publication.getResponsePayload()
        );
    }

    private GeneratedContent approvedContent() {
        return content(ContentStatus.APPROVED, OffsetDateTime.parse("2026-06-09T10:05:00Z"));
    }

    private GeneratedContent pendingReviewContent() {
        return content(ContentStatus.PENDING_REVIEW, null);
    }

    private GeneratedContent content(ContentStatus status, OffsetDateTime approvedAt) {
        return new GeneratedContent(
                10L,
                20L,
                1L,
                "TELEGRAM",
                "INFORMATIVO",
                "Titulo",
                "Mensaje",
                status,
                OffsetDateTime.parse("2026-06-09T10:00:00Z"),
                approvedAt
        );
    }
}
