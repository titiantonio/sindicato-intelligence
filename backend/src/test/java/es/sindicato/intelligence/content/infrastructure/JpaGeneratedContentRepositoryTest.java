package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaGeneratedContentRepositoryTest {

    @Autowired
    private GeneratedContentRepository contentRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void savesAndFindsContentByEventId() {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));

        GeneratedContent saved = contentRepository.save(content(event.getId()));

        Optional<GeneratedContent> foundById = contentRepository.findById(saved.getId());
        List<GeneratedContent> foundByEvent = contentRepository.findByEventId(event.getId());
        assertNotNull(saved.getId());
        assertTrue(foundById.isPresent());
        assertEquals("Titulo", foundById.get().getTitle());
        assertEquals(1, foundByEvent.size());
        assertEquals(ContentStatus.PENDING_REVIEW, foundByEvent.getFirst().getStatus());
    }

    private GeneratedContent content(Long eventId) {
        return new GeneratedContent(null, eventId, 1L, "TELEGRAM", "INFORMATIVO", "Titulo", "Mensaje", ContentStatus.PENDING_REVIEW, OffsetDateTime.parse("2026-06-08T10:00:00Z"), null);
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Source(null, "Fuente Content", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new NewsArticle(null, sourceId, "CCOO mantiene movilizaciones 0-3", uniqueUrl("news"), "Resumen", "Contenido", uniqueHash(), now.minusHours(1), now, NewsStatus.EVENT_MATCHED, now, now);
    }

    private Event event(Long newsId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(null, "Evento sindical", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(newsId), now, now, now, now);
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String uniqueHash() {
        String value = UUID.randomUUID().toString().replace("-", "");
        return value + value;
    }
}
