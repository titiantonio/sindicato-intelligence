package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsClassificationRepository classificationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void detectsEventCreatingNewEventWhenNoMatchExists() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-new-event"), hash('a'), NewsStatus.CLASSIFIED));
        classificationRepository.save(classification(newsArticle.getId()));

        mockMvc.perform(post("/api/v1/events/detect")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newsId": %d
                                }
                                """.formatted(newsArticle.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId", notNullValue()))
                .andExpect(jsonPath("$.newsId").value(newsArticle.getId()))
                .andExpect(jsonPath("$.created").value(true))
                .andExpect(jsonPath("$.matched").value(false))
                .andExpect(jsonPath("$.eventStatus").value("OPEN"));

        assertTrue(eventRepository.existsNewsAssociation(newsArticle.getId()));
        assertEquals(NewsStatus.EVENT_MATCHED, newsRepository.findById(newsArticle.getId()).orElseThrow().getProcessingStatus());
    }

    @Test
    void detectsEventAssociatingNewsToExistingEvent() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle existingNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-existing-event"), hash('b'), NewsStatus.EVENT_MATCHED));
        Event existingEvent = eventRepository.save(event(existingNews.getId()));
        NewsArticle newNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-match-event"), hash('c'), NewsStatus.CLASSIFIED));
        classificationRepository.save(classification(newNews.getId()));

        mockMvc.perform(post("/api/v1/events/detect")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newsId": %d
                                }
                                """.formatted(newNews.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(existingEvent.getId()))
                .andExpect(jsonPath("$.newsId").value(newNews.getId()))
                .andExpect(jsonPath("$.created").value(false))
                .andExpect(jsonPath("$.matched").value(true))
                .andExpect(jsonPath("$.confidence", greaterThanOrEqualTo(85)))
                .andExpect(jsonPath("$.eventStatus").value("OPEN"));

        Event updatedEvent = eventRepository.findById(existingEvent.getId()).orElseThrow();
        assertEquals(Set.of(existingNews.getId(), newNews.getId()), updatedEvent.getNewsIds());
        assertEquals(NewsStatus.EVENT_MATCHED, newsRepository.findById(newNews.getId()).orElseThrow().getProcessingStatus());
    }


    @Test
    void listsAndGetsEventDetail() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-list-event"), hash('d'), NewsStatus.EVENT_MATCHED));
        classificationRepository.save(classification(newsArticle.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));

        mockMvc.perform(get("/api/v1/events").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(event.getId().intValue())));

        mockMvc.perform(get("/api/v1/events/{id}", event.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.news[0].id").value(newsArticle.getId()))
                .andExpect(jsonPath("$.news[0].classification.newsId").value(newsArticle.getId()))
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.analyses").isArray());
    }

    @Test
    void hidesEventsBackedOnlyByDiscardableNews() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-discarded-event"), hash('e'), NewsStatus.EVENT_MATCHED));
        classificationRepository.save(discardableClassification(newsArticle.getId()));
        Event event = eventRepository.save(outOfScopeEvent(newsArticle.getId()));

        mockMvc.perform(get("/api/v1/events").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItem(event.getId().intValue()))));

        mockMvc.perform(get("/api/v1/events/{id}", event.getId()).with(adminJwt()))
                .andExpect(status().isNotFound());
    }

    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }
    private Source source() {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Source(
                null,
                "Fuente Event API",
                uniqueUrl("sources"),
                "RSS",
                10,
                true,
                now,
                now
        );
    }

    private NewsArticle newsArticle(Long sourceId, String url, String hash, NewsStatus status) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new NewsArticle(
                null,
                sourceId,
                "Nueva adjudicacion SIPRI mayo 2026",
                url,
                "La Consejeria publica la adjudicacion SIPRI de mayo.",
                "El procedimiento afecta a interinos docentes.",
                hash,
                now.minusHours(1),
                now,
                status,
                now,
                now
        );
    }

    private NewsClassification classification(Long newsId) {
        return new NewsClassification(
                null,
                newsId,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(95),
                ImpactLevel.HIGH,
                UrgencyLevel.HIGH,
                List.of("SIPRI", "adjudicacion"),
                List.of("Consejeria"),
                OffsetDateTime.now()
        );
    }

    private NewsClassification discardableClassification(Long newsId) {
        return new NewsClassification(
                null,
                newsId,
                ClassificationCategory.OTROS,
                "FUERA_DE_AMBITO",
                BigDecimal.ZERO,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                OffsetDateTime.now()
        );
    }

    private Event event(Long newsId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Event(
                null,
                "Adjudicacion SIPRI mayo 2026",
                "Evento sobre adjudicacion SIPRI de mayo",
                EventCategory.SIPRI,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(newsId),
                now,
                now,
                now,
                now
        );
    }

    private Event outOfScopeEvent(Long newsId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Event(
                null,
                "Sorprenden a 13 personas revendiendo entradas",
                "Evento fuera de ambito",
                EventCategory.OTROS,
                Importance.LOW,
                EventStatus.OPEN,
                Set.of(newsId),
                now,
                now,
                now,
                now
        );
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String hash(char character) {
        return String.valueOf(character).repeat(64);
    }
}
