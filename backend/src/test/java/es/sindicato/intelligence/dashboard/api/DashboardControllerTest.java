package es.sindicato.intelligence.dashboard.api;

import com.jayway.jsonpath.JsonPath;
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
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardControllerTest {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("Europe/Madrid");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GeneratedContentRepository contentRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    void returnsDashboardSnapshotWithDailyMetricsAndPriorityEvents() throws Exception {
        Source source = sourceRepository.save(source());
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime today = now.minusHours(1);
        OffsetDateTime yesterday = now.minusDays(1);
        OffsetDateTime twoDaysAgo = now.minusDays(2);
        OffsetDateTime latestNewsUpdate = now.plusYears(1);
        OffsetDateTime latestEventUpdate = now.plusYears(2);
        OffsetDateTime latestContentUpdate = now.plusYears(3);
        OffsetDateTime latestPublicationUpdate = now.plusYears(4);

        NewsArticle todayNews = newsRepository.save(newsArticle(source.getId(), "Noticia Hoy", today));
        newsRepository.save(newsArticle(source.getId(), "Noticia Ayer", yesterday));
        newsRepository.save(newsArticle(source.getId(), "Noticia Antigua", twoDaysAgo));
        NewsArticle latestNews = newsRepository.save(newsArticle(source.getId(), "Noticia Ultima", latestNewsUpdate));
        Event highToday = eventRepository.save(event(todayNews.getId(), "Evento High Hoy", EventCategory.SINDICAL, Importance.HIGH, EventStatus.OPEN, today));
        Event criticalToday = eventRepository.save(event(todayNews.getId(), "Evento Critical Hoy", EventCategory.SIPRI, Importance.CRITICAL, EventStatus.MONITORING, today.plusMinutes(1)));
        Event latestEvent = eventRepository.save(event(latestNews.getId(), "Evento Ultima Actualizacion", EventCategory.SINDICAL, Importance.CRITICAL, EventStatus.OPEN, latestEventUpdate));
        eventRepository.save(event(todayNews.getId(), "Evento Low", EventCategory.SINDICAL, Importance.LOW, EventStatus.OPEN, today.plusMinutes(2)));
        eventRepository.save(event(todayNews.getId(), "Evento Cerrado", EventCategory.SINDICAL, Importance.CRITICAL, EventStatus.CLOSED, today.plusMinutes(3)));
        eventRepository.save(event(todayNews.getId(), "Evento Otros", EventCategory.OTROS, Importance.CRITICAL, EventStatus.OPEN, today.plusMinutes(4)));
        eventRepository.save(event(todayNews.getId(), "Evento Ayer", EventCategory.SINDICAL, Importance.HIGH, EventStatus.OPEN, yesterday));

        for (int index = 0; index < 12; index++) {
            NewsArticle priorityNews = newsRepository.save(newsArticle(source.getId(), "Noticia Prioritaria " + index, today.plusMinutes(10 + index)));
            eventRepository.save(event(priorityNews.getId(), "Evento Critico " + index, EventCategory.SINDICAL, Importance.CRITICAL, EventStatus.OPEN, today.plusMinutes(10 + index)));
        }

        contentRepository.save(content(highToday.getId(), ContentStatus.PENDING_REVIEW, today));
        contentRepository.save(content(highToday.getId(), ContentStatus.PENDING_REVIEW, yesterday));
        contentRepository.save(content(highToday.getId(), ContentStatus.GENERATED, today.plusMinutes(4)));
        contentRepository.save(content(highToday.getId(), ContentStatus.APPROVED, today.plusMinutes(5)));
        contentRepository.save(content(latestEvent.getId(), ContentStatus.GENERATED, latestContentUpdate));
        GeneratedContent publicationContent = content(criticalToday.getId(), ContentStatus.APPROVED, today.plusMinutes(8));
        Publication publishedToday = Publication.pending(content(criticalToday.getId(), ContentStatus.APPROVED, today).getId(), "TELEGRAM");
        publishedToday.markPublished("msg-today", today, "{}");
        publicationRepository.save(publishedToday);
        Publication publishedYesterday = Publication.pending(content(criticalToday.getId(), ContentStatus.APPROVED, yesterday).getId(), "TELEGRAM");
        publishedYesterday.markPublished("msg-yesterday", yesterday, "{}");
        publicationRepository.save(publishedYesterday);
        publicationRepository.save(Publication.scheduled(content(criticalToday.getId(), ContentStatus.APPROVED, today.plusMinutes(6)).getId(), "TELEGRAM", today.plusDays(1)));
        Publication failedPublication = Publication.pending(content(criticalToday.getId(), ContentStatus.APPROVED, today.plusMinutes(7)).getId(), "TELEGRAM");
        failedPublication.markFailed("{}");
        publicationRepository.save(failedPublication);
        Publication latestPublication = Publication.pending(publicationContent.getId(), "TELEGRAM");
        latestPublication.markPublished("msg-latest", latestPublicationUpdate, "{}");
        publicationRepository.save(latestPublication);

        DateRange todayRange = todayRange();
        DateRange yesterdayRange = new DateRange(todayRange.start().minusDays(1), todayRange.start());
        long todayNewsCount = countInRange(newsRepository.findAll(), NewsArticle::getCapturedAt, todayRange);
        long yesterdayNewsCount = countInRange(newsRepository.findAll(), NewsArticle::getCapturedAt, yesterdayRange);
        long todayEvents = countInRange(eventRepository.findAll(), Event::getFirstDetectedAt, todayRange);
        long yesterdayEvents = countInRange(eventRepository.findAll(), Event::getFirstDetectedAt, yesterdayRange);
        long todayPendingContents = countInRange(
                contentRepository.findAll().stream().filter(content -> content.getStatus() == ContentStatus.PENDING_REVIEW).toList(),
                GeneratedContent::getGeneratedAt,
                todayRange
        );
        long yesterdayPendingContents = countInRange(
                contentRepository.findAll().stream().filter(content -> content.getStatus() == ContentStatus.PENDING_REVIEW).toList(),
                GeneratedContent::getGeneratedAt,
                yesterdayRange
        );
        long todayPublications = countInRange(
                publicationRepository.findAll().stream().filter(publication -> publication.getStatus() == PublicationStatus.PUBLISHED).toList(),
                Publication::getPublishedAt,
                todayRange
        );
        long yesterdayPublications = countInRange(
                publicationRepository.findAll().stream().filter(publication -> publication.getStatus() == PublicationStatus.PUBLISHED).toList(),
                Publication::getPublishedAt,
                yesterdayRange
        );
        long totalNews = newsRepository.findAll().size();
        long criticalEvents = eventRepository.findAll().stream()
                .filter(Event::isActive)
                .filter(event -> event.getImportance() == Importance.CRITICAL)
                .count();
        long pendingContents = contentRepository.findAll().stream()
                .filter(content -> content.getStatus() == ContentStatus.PENDING_REVIEW)
                .count();
        long generatedContents = contentRepository.findAll().stream()
                .filter(content -> content.getStatus() == ContentStatus.GENERATED)
                .count();
        long approvedContents = contentRepository.findAll().stream()
                .filter(content -> content.getStatus() == ContentStatus.APPROVED)
                .count();
        long scheduledPublications = publicationRepository.findAll().stream()
                .filter(publication -> publication.getStatus() == PublicationStatus.SCHEDULED)
                .count();
        long failedPublications = publicationRepository.findAll().stream()
                .filter(publication -> publication.getStatus() == PublicationStatus.FAILED)
                .count();

        mockMvc.perform(get("/api/v1/dashboard").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricCards", notNullValue()))
                .andExpect(jsonPath("$.metricCards[0].label").value("Noticias"))
                .andExpect(jsonPath("$.metricCards[0].title").value("Noticias"))
                .andExpect(jsonPath("$.metricCards[0].subtitle").value("Ultima captura"))
                .andExpect(jsonPath("$.metricCards[0].icon").value("news"))
                .andExpect(jsonPath("$.metricCards[0].badgeLabel").value("Hoy"))
                .andExpect(jsonPath("$.metricCards[0].lastUpdatedAt").value(formatDate(latestNewsUpdate)))
                .andExpect(jsonPath("$.metricCards[0].todayValue").value(todayNewsCount))
                .andExpect(jsonPath("$.metricCards[0].yesterdayValue").value(yesterdayNewsCount))
                .andExpect(jsonPath("$.metricCards[0].difference").value(todayNewsCount - yesterdayNewsCount))
                .andExpect(jsonPath("$.metricCards[0].items", hasSize(3)))
                .andExpect(jsonPath("$.metricCards[0].items[0].label").value("Capturadas hoy"))
                .andExpect(jsonPath("$.metricCards[0].items[0].value").value(todayNewsCount))
                .andExpect(jsonPath("$.metricCards[0].items[1].label").value("Diferencia vs anterior"))
                .andExpect(jsonPath("$.metricCards[0].items[1].value").value(todayNewsCount - yesterdayNewsCount))
                .andExpect(jsonPath("$.metricCards[0].items[1].signed").value(true))
                .andExpect(jsonPath("$.metricCards[0].items[2].label").value("Total acumulado"))
                .andExpect(jsonPath("$.metricCards[0].items[2].value").value(totalNews))
                .andExpect(jsonPath("$.metricCards[1].label").value("Eventos"))
                .andExpect(jsonPath("$.metricCards[1].lastUpdatedAt").value(formatDate(latestEventUpdate)))
                .andExpect(jsonPath("$.metricCards[1].todayValue").value(todayEvents))
                .andExpect(jsonPath("$.metricCards[1].yesterdayValue").value(yesterdayEvents))
                .andExpect(jsonPath("$.metricCards[1].difference").value(todayEvents - yesterdayEvents))
                .andExpect(jsonPath("$.metricCards[1].items[0].value").value(todayEvents))
                .andExpect(jsonPath("$.metricCards[1].items[1].value").value(criticalEvents))
                .andExpect(jsonPath("$.metricCards[1].items[2].value").value(pendingContents))
                .andExpect(jsonPath("$.metricCards[2].label").value("Contenidos"))
                .andExpect(jsonPath("$.metricCards[2].lastUpdatedAt").value(formatDate(latestContentUpdate)))
                .andExpect(jsonPath("$.metricCards[2].todayValue").value(todayPendingContents))
                .andExpect(jsonPath("$.metricCards[2].yesterdayValue").value(yesterdayPendingContents))
                .andExpect(jsonPath("$.metricCards[2].difference").value(todayPendingContents - yesterdayPendingContents))
                .andExpect(jsonPath("$.metricCards[2].items[0].value").value(pendingContents))
                .andExpect(jsonPath("$.metricCards[2].items[1].value").value(generatedContents))
                .andExpect(jsonPath("$.metricCards[2].items[2].value").value(approvedContents))
                .andExpect(jsonPath("$.metricCards[3].label").value("Publicaciones"))
                .andExpect(jsonPath("$.metricCards[3].lastUpdatedAt").value(formatDate(latestPublicationUpdate)))
                .andExpect(jsonPath("$.metricCards[3].todayValue").value(todayPublications))
                .andExpect(jsonPath("$.metricCards[3].yesterdayValue").value(yesterdayPublications))
                .andExpect(jsonPath("$.metricCards[3].difference").value(todayPublications - yesterdayPublications))
                .andExpect(jsonPath("$.metricCards[3].items[0].value").value(todayPublications))
                .andExpect(jsonPath("$.metricCards[3].items[1].value").value(scheduledPublications))
                .andExpect(jsonPath("$.metricCards[3].items[2].value").value(failedPublications))
                .andExpect(jsonPath("$.priorityEvents", hasSize(10)))
                .andExpect(jsonPath("$.priorityEvents[*].importance", everyItem(containsString("CRITICAL"))))
                .andExpect(jsonPath("$.priorityEvents[*].category", everyItem(not(containsString("OTROS")))))
                .andExpect(jsonPath("$.priorityEvents[*].status", everyItem(not(containsString("CLOSED")))));
    }

    @Test
    void ordersPriorityEventsByImpactNewsCountAndLastUpdate() throws Exception {
        Source source = sourceRepository.save(source());
        OffsetDateTime now = OffsetDateTime.now();
        NewsArticle firstNews = newsRepository.save(newsArticle(source.getId(), "Noticia 1", now.minusMinutes(30)));
        NewsArticle secondNews = newsRepository.save(newsArticle(source.getId(), "Noticia 2", now.minusMinutes(25)));
        NewsArticle thirdNews = newsRepository.save(newsArticle(source.getId(), "Noticia 3", now.minusMinutes(20)));
        NewsArticle fourthNews = newsRepository.save(newsArticle(source.getId(), "Noticia 4", now.minusMinutes(15)));
        NewsArticle fifthNews = newsRepository.save(newsArticle(source.getId(), "Noticia 5", now.minusMinutes(14)));
        NewsArticle sixthNews = newsRepository.save(newsArticle(source.getId(), "Noticia 6", now.minusMinutes(13)));
        NewsArticle seventhNews = newsRepository.save(newsArticle(source.getId(), "Noticia 7", now.minusMinutes(12)));
        NewsArticle eighthNews = newsRepository.save(newsArticle(source.getId(), "Noticia 8", now.minusMinutes(11)));
        String suffix = UUID.randomUUID().toString();
        String highTitle = "High con mas noticias " + suffix;
        String criticalOneTitle = "Critical con una noticia " + suffix;
        String criticalMoreTitle = "Critical con mas noticias " + suffix;

        eventRepository.save(event(Set.of(firstNews.getId(), fifthNews.getId(), sixthNews.getId(), seventhNews.getId(), eighthNews.getId()), highTitle, EventCategory.SINDICAL, Importance.HIGH, EventStatus.OPEN, now.plusHours(3)));
        eventRepository.save(event(Set.of(secondNews.getId()), criticalOneTitle, EventCategory.SIPRI, Importance.CRITICAL, EventStatus.OPEN, now.plusHours(2)));
        eventRepository.save(event(Set.of(thirdNews.getId(), fourthNews.getId()), criticalMoreTitle, EventCategory.SINDICAL, Importance.CRITICAL, EventStatus.OPEN, now.plusHours(1)));

        MvcResult result = mockMvc.perform(get("/api/v1/dashboard").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityEvents[0].title").value(criticalMoreTitle))
                .andExpect(jsonPath("$.priorityEvents[0].relatedNews").value(2))
                .andExpect(jsonPath("$.priorityEvents[1].title").value(criticalOneTitle))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        java.util.List<String> titles = JsonPath.read(response, "$.priorityEvents[*].title");
        assertEquals(criticalMoreTitle, titles.getFirst());
        assertTrue(titles.indexOf(criticalOneTitle) < titles.indexOf(highTitle));
    }

    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Source(null, "Fuente Dashboard API", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId, String title, OffsetDateTime capturedAt) {
        return new NewsArticle(null, sourceId, title, uniqueUrl("news"), "Resumen", "Contenido", uniqueHash(), capturedAt.minusHours(1), capturedAt, NewsStatus.EVENT_MATCHED, capturedAt, capturedAt);
    }

    private Event event(
            Long newsId,
            String title,
            EventCategory category,
            Importance importance,
            EventStatus status,
            OffsetDateTime detectedAt
    ) {
        return event(Set.of(newsId), title, category, importance, status, detectedAt);
    }

    private Event event(
            Set<Long> newsIds,
            String title,
            EventCategory category,
            Importance importance,
            EventStatus status,
            OffsetDateTime detectedAt
    ) {
        return new Event(null, title, "Descripcion", category, importance, status, newsIds, detectedAt, detectedAt, detectedAt, detectedAt);
    }

    private GeneratedContent content(Long eventId, ContentStatus status, OffsetDateTime generatedAt) {
        return contentRepository.save(new GeneratedContent(null, eventId, 1L, "TELEGRAM", "INFORMATIVO", "Titulo", "Mensaje", status, generatedAt, status == ContentStatus.APPROVED ? generatedAt.plusMinutes(1) : null));
    }

    private <T> long countInRange(Iterable<T> items, Function<T, OffsetDateTime> dateExtractor, DateRange range) {
        long count = 0;
        for (T item : items) {
            OffsetDateTime value = dateExtractor.apply(item);
            if (value != null && !value.isBefore(range.start()) && value.isBefore(range.end())) {
                count++;
            }
        }

        return count;
    }

    private DateRange todayRange() {
        LocalDate today = LocalDate.now(DASHBOARD_ZONE);
        OffsetDateTime start = today.atStartOfDay(DASHBOARD_ZONE).toOffsetDateTime();
        return new DateRange(start, start.plusDays(1));
    }

    private String formatDate(OffsetDateTime value) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value);
    }

    private record DateRange(OffsetDateTime start, OffsetDateTime end) {
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String uniqueHash() {
        String value = UUID.randomUUID().toString().replace("-", "");
        return value + value;
    }
}
