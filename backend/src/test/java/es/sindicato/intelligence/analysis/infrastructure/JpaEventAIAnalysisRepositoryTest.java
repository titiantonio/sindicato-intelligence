package es.sindicato.intelligence.analysis.infrastructure;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
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
class JpaEventAIAnalysisRepositoryTest {

    @Autowired
    private EventAIAnalysisRepository analysisRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void savesAndFindsAnalysisByEventId() {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));

        EventAIAnalysis saved = analysisRepository.save(analysis(event.getId()));

        Optional<EventAIAnalysis> foundById = analysisRepository.findById(saved.getId());
        List<EventAIAnalysis> foundByEvent = analysisRepository.findByEventId(event.getId());
        assertNotNull(saved.getId());
        assertTrue(foundById.isPresent());
        assertEquals("Resumen ejecutivo", foundById.get().getExecutiveSummary());
        assertEquals(1, foundByEvent.size());
        assertEquals(List.of("Punto clave"), foundByEvent.getFirst().getKeyPoints());
        assertEquals(List.of("Riesgo"), foundByEvent.getFirst().getRisks());
        assertEquals(List.of("Oportunidad"), foundByEvent.getFirst().getOpportunities());
    }

    @Test
    void findsLatestAnalysisByEventId() {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));

        analysisRepository.save(analysis(event.getId(), OffsetDateTime.parse("2026-06-08T10:00:00Z")));
        EventAIAnalysis latest = analysisRepository.save(analysis(event.getId(), OffsetDateTime.parse("2026-06-08T11:00:00Z")));

        Optional<EventAIAnalysis> found = analysisRepository.findLatestByEventId(event.getId());

        assertTrue(found.isPresent());
        assertEquals(latest.getId(), found.get().getId());
    }

    private EventAIAnalysis analysis(Long eventId) {
        return analysis(eventId, OffsetDateTime.parse("2026-06-08T10:00:00Z"));
    }

    private EventAIAnalysis analysis(Long eventId, OffsetDateTime generatedAt) {
        return new EventAIAnalysis(
                null,
                eventId,
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                "deterministic-analysis",
                generatedAt
        );
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Source(null, "Fuente Analysis", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new NewsArticle(
                null,
                sourceId,
                "CCOO mantiene movilizaciones 0-3",
                uniqueUrl("news"),
                "Resumen",
                "Contenido",
                uniqueHash(),
                now.minusHours(1),
                now,
                NewsStatus.EVENT_MATCHED,
                now,
                now
        );
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
