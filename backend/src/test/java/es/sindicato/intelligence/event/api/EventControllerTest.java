package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
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
import org.junit.jupiter.api.BeforeEach;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(properties = {
        "app.automation.scheduler.enabled=false",
        "app.publication.scheduler.enabled=false"
})
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

    @Autowired
    private EventAIAnalysisRepository analysisRepository;

    @Autowired
    private GeneratedContentRepository contentRepository;

    @Autowired
    private AiProviderSettingRepository providerSettingRepository;

    @Autowired
    private AiWorkflowSettingRepository workflowSettingRepository;

    @BeforeEach
    void useDeterministicEventMatchingProvider() {
        OffsetDateTime now = OffsetDateTime.now();
        eventRepository.findAll().stream()
                .filter(event -> event.getStatus() != EventStatus.ARCHIVED)
                .forEach(event -> {
                    event.archive(now);
                    eventRepository.save(event);
                });

        AiProviderSetting provider = providerSettingRepository.findByCode("deterministic")
                .orElse(new AiProviderSetting("deterministic", "Deterministic", true, null, now, now));
        provider.update(true, null, false, now);
        providerSettingRepository.save(provider);

        AiWorkflowSetting workflow = workflowSettingRepository.findByWorkflowCode("WF03_EVENT_MATCHING")
                .orElse(new AiWorkflowSetting("WF03_EVENT_MATCHING", "deterministic", "deterministic-event-matching", BigDecimal.ZERO, 1024, 0, now, now));
        workflow.update("deterministic", "deterministic-event-matching", BigDecimal.ZERO, 1024, 0, now);
        workflowSettingRepository.save(workflow);
    }

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
                .andExpect(jsonPath("$.matchDecision").value("NEW_EVENT"))
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
                .andExpect(jsonPath("$.matchDecision").value("AUTOMATIC_MATCH"))
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
                .andExpect(jsonPath("$.editorialStatus").value("PENDING_ANALYSIS"))
                .andExpect(jsonPath("$.news[0].id").value(newsArticle.getId()))
                .andExpect(jsonPath("$.news[0].classification.newsId").value(newsArticle.getId()))
                .andExpect(jsonPath("$.contents").isArray())
                .andExpect(jsonPath("$.analyses").isArray());
    }

    @Test
    void listsEventsByImpactAndNewsCountPriority() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle criticalNewsA = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-critical-a"), hash('f'), NewsStatus.EVENT_MATCHED));
        NewsArticle criticalNewsB = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-critical-b"), hash('g'), NewsStatus.EVENT_MATCHED));
        NewsArticle criticalNewsC = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-critical-c"), hash('h'), NewsStatus.EVENT_MATCHED));
        Event critical = eventRepository.save(event(
                Set.of(criticalNewsA.getId(), criticalNewsB.getId(), criticalNewsC.getId()),
                "Evento critical varios",
                Importance.CRITICAL
        ));

        mockMvc.perform(get("/api/v1/events").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(critical.getId().intValue())))
                .andExpect(jsonPath("$[0].importance").value("CRITICAL"))
                .andExpect(jsonPath("$[0].newsCount").value(3));
    }

    @Test
    void discardsActiveEventManually() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-discard-manual"), hash('j'), NewsStatus.EVENT_MATCHED));
        Event event = eventRepository.save(event(newsArticle.getId()));

        mockMvc.perform(post("/api/v1/events/{id}/discard", event.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.editorialStatus").value("DISCARDED"));

        mockMvc.perform(get("/api/v1/events").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(event.getId().intValue())))
                .andExpect(jsonPath("$[?(@.id == %d)].editorialStatus".formatted(event.getId().intValue()), hasItem("DISCARDED")));

        mockMvc.perform(post("/api/v1/events/{id}/restore", event.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.editorialStatus").value("PENDING_ANALYSIS"));
    }

    @Test
    void exposesEditorialStatusForAnalyzedAndPublishedEvents() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle analyzedNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-analyzed-event"), hash('k'), NewsStatus.EVENT_MATCHED));
        Event analyzedEvent = eventRepository.save(event(analyzedNews.getId()));
        analysisRepository.save(analysis(analyzedEvent.getId()));

        NewsArticle publishedNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-published-event"), hash('l'), NewsStatus.EVENT_MATCHED));
        Event publishedEvent = eventRepository.save(event(publishedNews.getId(), "Evento publicado", Importance.HIGH));
        contentRepository.save(content(publishedEvent.getId(), ContentStatus.PUBLISHED));

        mockMvc.perform(get("/api/v1/events/{id}", analyzedEvent.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.editorialStatus").value("ANALYZED_PENDING_CONTENT"));

        mockMvc.perform(get("/api/v1/events/{id}", publishedEvent.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.editorialStatus").value("PUBLISHED"));
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
        return event(newsId, "Adjudicacion SIPRI mayo 2026", Importance.HIGH);
    }

    private Event event(Long newsId, String title, Importance importance) {
        return event(Set.of(newsId), title, importance);
    }

    private Event event(Set<Long> newsIds, String title, Importance importance) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Event(
                null,
                title,
                "Evento sobre adjudicacion SIPRI de mayo",
                EventCategory.SIPRI,
                importance,
                EventStatus.OPEN,
                newsIds,
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

    private EventAIAnalysis analysis(Long eventId) {
        return new EventAIAnalysis(
                null,
                eventId,
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Clave"),
                List.of(),
                List.of(),
                "deterministic",
                OffsetDateTime.now()
        );
    }

    private GeneratedContent content(Long eventId, ContentStatus status) {
        return new GeneratedContent(
                null,
                eventId,
                1L,
                "TELEGRAM",
                "INFORMATIVO",
                "Titulo",
                "Contenido",
                status,
                OffsetDateTime.now(),
                status == ContentStatus.APPROVED || status == ContentStatus.PUBLISHED ? OffsetDateTime.now() : null
        );
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String hash(char character) {
        return String.valueOf(character).repeat(64);
    }
}
