package es.sindicato.intelligence.news.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsArticleTest {

    @Test
    void createsNewsArticle() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        NewsArticle newsArticle = newsArticle(now, NewsStatus.CAPTURED);

        assertEquals(1L, newsArticle.getId());
        assertEquals(2L, newsArticle.getSourceId());
        assertEquals("Convocatoria docente", newsArticle.getTitle());
        assertEquals("https://test.example/news/1", newsArticle.getUrl());
        assertEquals(NewsStatus.CAPTURED, newsArticle.getProcessingStatus());
        assertEquals(now, newsArticle.getCreatedAt());
        assertEquals(now, newsArticle.getUpdatedAt());
    }

    @Test
    void changesStatusAndUpdatesTimestamp() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        NewsArticle newsArticle = newsArticle(createdAt, NewsStatus.CAPTURED);

        newsArticle.changeStatus(NewsStatus.CLASSIFIED, updatedAt);

        assertEquals(NewsStatus.CLASSIFIED, newsArticle.getProcessingStatus());
        assertEquals(updatedAt, newsArticle.getUpdatedAt());
    }

    @Test
    void marksNewsAsDiscarded() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        NewsArticle newsArticle = newsArticle(createdAt, NewsStatus.CAPTURED);

        newsArticle.markDiscarded();

        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    @Test
    void rejectsInvalidHash() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new NewsArticle(
                1L,
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                "invalid",
                now,
                now,
                NewsStatus.CAPTURED,
                now,
                now
        ));
    }

    private NewsArticle newsArticle(OffsetDateTime timestamp, NewsStatus status) {
        return new NewsArticle(
                1L,
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                timestamp,
                timestamp,
                status,
                timestamp,
                timestamp
        );
    }
}
