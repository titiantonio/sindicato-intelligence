package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventVisibilityPolicyTest {

    @Test
    void hidesEventWhenAllNewsAreDiscardable() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventVisibilityPolicy policy = new EventVisibilityPolicy(newsRepository, classificationRepository);
        NewsArticle newsArticle = newsArticle(1L, NewsStatus.EVENT_MATCHED);
        Event event = event(Set.of(newsArticle.getId()), EventStatus.OPEN);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(discardableClassification(newsArticle.getId())));

        assertFalse(policy.isVisible(event));
    }

    @Test
    void showsEventWhenAtLeastOneNewsIsRelevant() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventVisibilityPolicy policy = new EventVisibilityPolicy(newsRepository, classificationRepository);
        NewsArticle discardedNews = newsArticle(1L, NewsStatus.EVENT_MATCHED);
        NewsArticle relevantNews = newsArticle(2L, NewsStatus.EVENT_MATCHED);
        Event event = event(Set.of(discardedNews.getId(), relevantNews.getId()), EventStatus.OPEN);

        when(newsRepository.findById(discardedNews.getId())).thenReturn(Optional.of(discardedNews));
        when(newsRepository.findById(relevantNews.getId())).thenReturn(Optional.of(relevantNews));
        when(classificationRepository.findByNewsId(discardedNews.getId())).thenReturn(Optional.of(discardableClassification(discardedNews.getId())));
        when(classificationRepository.findByNewsId(relevantNews.getId())).thenReturn(Optional.of(relevantClassification(relevantNews.getId())));

        assertTrue(policy.isVisible(event));
    }

    @Test
    void hidesArchivedEventsEvenWhenNewsAreRelevant() {
        EventVisibilityPolicy policy = new EventVisibilityPolicy(mock(NewsRepository.class), mock(NewsClassificationRepository.class));

        assertFalse(policy.isVisible(event(Set.of(1L), EventStatus.ARCHIVED)));
    }

    private NewsArticle newsArticle(Long id, NewsStatus status) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-25T10:00:00Z");
        return new NewsArticle(
                id,
                1L,
                "Noticia",
                "https://test.example/news/" + id,
                "Resumen",
                "Contenido",
                "a".repeat(64),
                now.minusHours(1),
                now,
                status,
                now,
                now
        );
    }

    private Event event(Set<Long> newsIds, EventStatus status) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-25T10:00:00Z");
        return new Event(null, "Evento", "Descripcion", EventCategory.SINDICAL, Importance.HIGH, status, newsIds, now, now, now, now);
    }

    private NewsClassification discardableClassification(Long newsId) {
        return classification(newsId, ClassificationCategory.OTROS, "FUERA_DE_AMBITO", BigDecimal.ZERO);
    }

    private NewsClassification relevantClassification(Long newsId) {
        return classification(newsId, ClassificationCategory.SINDICAL, "Laboral", BigDecimal.valueOf(80));
    }

    private NewsClassification classification(Long newsId, ClassificationCategory category, String subcategory, BigDecimal relevance) {
        return new NewsClassification(
                null,
                newsId,
                category,
                subcategory,
                relevance,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                OffsetDateTime.parse("2026-06-25T10:00:00Z")
        );
    }
}
