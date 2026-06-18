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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PublishScheduledPublicationsUseCaseTest {

    @Test
    void publishesDueScheduledPublication() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        PublishingProvider provider = mock(PublishingProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishScheduledPublicationsUseCase useCase = new PublishScheduledPublicationsUseCase(contentRepository, publicationRepository, List.of(provider), audit);
        OffsetDateTime now = OffsetDateTime.parse("2026-06-10T12:00:00Z");
        Publication publication = Publication.scheduled(10L, "TELEGRAM", now.minusMinutes(1));
        GeneratedContent content = content();

        when(publicationRepository.findDueScheduled(now, 50)).thenReturn(List.of(publication));
        when(contentRepository.findById(10L)).thenReturn(Optional.of(content));
        when(provider.supports("TELEGRAM")).thenReturn(true);
        when(provider.publish(any(PublishingRequest.class))).thenReturn(new PublishingResult("message-123", "{\"ok\":true}"));

        int processed = useCase.execute(now, 50);

        assertEquals(1, processed);
        ArgumentCaptor<Publication> captor = ArgumentCaptor.forClass(Publication.class);
        verify(publicationRepository).save(captor.capture());
        assertEquals(PublicationStatus.PUBLISHED, captor.getValue().getStatus());
        assertEquals(ContentStatus.PUBLISHED, content.getStatus());
        verify(audit).record(eq("PUBLICATION_PUBLISHED"), eq("PUBLICATION"), eq(null), eq(null), any());
    }

    @Test
    void marksPublicationAsFailedWhenProviderFails() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        PublishingProvider provider = mock(PublishingProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishScheduledPublicationsUseCase useCase = new PublishScheduledPublicationsUseCase(contentRepository, publicationRepository, List.of(provider), audit);
        OffsetDateTime now = OffsetDateTime.parse("2026-06-10T12:00:00Z");
        Publication publication = Publication.scheduled(10L, "TELEGRAM", now.minusMinutes(1));

        when(publicationRepository.findDueScheduled(now, 50)).thenReturn(List.of(publication));
        when(contentRepository.findById(10L)).thenReturn(Optional.of(content()));
        when(provider.supports("TELEGRAM")).thenReturn(true);
        when(provider.publish(any(PublishingRequest.class))).thenThrow(new PublishingProviderException("provider unavailable"));

        useCase.execute(now, 50);

        ArgumentCaptor<Publication> captor = ArgumentCaptor.forClass(Publication.class);
        verify(publicationRepository).save(captor.capture());
        assertEquals(PublicationStatus.FAILED, captor.getValue().getStatus());
        verify(audit).record(eq("PUBLICATION_FAILED"), eq("PUBLICATION"), eq(null), eq(null), any());
    }

    @Test
    void marksPublicationAsFailedWhenProviderIsMissing() {
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        PublicationRepository publicationRepository = mock(PublicationRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        PublishScheduledPublicationsUseCase useCase = new PublishScheduledPublicationsUseCase(contentRepository, publicationRepository, List.of(), audit);
        OffsetDateTime now = OffsetDateTime.parse("2026-06-10T12:00:00Z");
        Publication publication = Publication.scheduled(10L, "TELEGRAM", now.minusMinutes(1));

        when(publicationRepository.findDueScheduled(now, 50)).thenReturn(List.of(publication));
        when(contentRepository.findById(10L)).thenReturn(Optional.of(content()));

        int processed = useCase.execute(now, 50);

        assertEquals(1, processed);
        ArgumentCaptor<Publication> captor = ArgumentCaptor.forClass(Publication.class);
        verify(publicationRepository).save(captor.capture());
        assertEquals(PublicationStatus.FAILED, captor.getValue().getStatus());
        verify(audit).record(eq("PUBLICATION_FAILED"), eq("PUBLICATION"), eq(null), eq(null), any());
    }

    private GeneratedContent content() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-10T10:00:00Z");
        return new GeneratedContent(10L, 20L, 1L, "TELEGRAM", "INFO", "Titulo", "Contenido", ContentStatus.APPROVED, generatedAt, generatedAt.plusHours(1));
    }
}
