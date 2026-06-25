package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DetectEventUseCaseTest {

    @Test
    void rejectsDiscardedNewsByStatus() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class)
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.DISCARDED);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new DetectEventCommand(newsArticle.getId())));

        verify(classificationRepository, never()).findByNewsId(any());
        verify(eventRepository, never()).save(any(Event.class));
        verify(aiProvider, never()).match(any(EventMatchingAIRequest.class));
    }

    @Test
    void discardsClassifiedNewsWithOutOfScopeClassificationWithoutCreatingEvent() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class)
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.CLASSIFIED);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(discardableClassification(newsArticle.getId())));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new DetectEventCommand(newsArticle.getId())));

        verify(newsRepository).save(newsArticle);
        verify(eventRepository, never()).findByStatusIn(any());
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventRepository, never()).saveNewsAssociation(any(), any(), any());
        verify(aiProvider, never()).match(any(EventMatchingAIRequest.class));
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    private NewsArticle newsArticle(NewsStatus status) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new NewsArticle(
                2L,
                1L,
                "Noticia fuera de ambito",
                "https://test.example/news/out-of-scope",
                "Resumen",
                "Contenido",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                status,
                now,
                now
        );
    }

    private NewsClassification discardableClassification(Long newsId) {
        return new NewsClassification(
                10L,
                newsId,
                ClassificationCategory.OTROS,
                "FUERA_DE_AMBITO",
                BigDecimal.ZERO,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                OffsetDateTime.parse("2026-06-15T10:00:00Z")
        );
    }
}
