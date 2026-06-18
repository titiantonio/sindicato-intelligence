package es.sindicato.intelligence.publication.api;

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
import es.sindicato.intelligence.publication.application.PublishingProvider;
import es.sindicato.intelligence.publication.application.PublishingRequest;
import es.sindicato.intelligence.publication.application.PublishingResult;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PublicationControllerTest {

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
    void publishesApprovedContent() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        GeneratedContent content = contentRepository.save(content(event.getId(), ContentStatus.APPROVED, OffsetDateTime.now()));

        mockMvc.perform(post("/api/v1/publications/{id}/publish", content.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.contentId").value(content.getId()))
                .andExpect(jsonPath("$.channel").value("TELEGRAM"))
                .andExpect(jsonPath("$.externalId").value("message-test"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.publishedAt", notNullValue()));

        GeneratedContent publishedContent = contentRepository.findById(content.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(ContentStatus.PUBLISHED, publishedContent.getStatus());

        mockMvc.perform(get("/api/v1/audit/editorial").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("PUBLICATION_PUBLISHED"))
                .andExpect(jsonPath("$[0].newValues").value(org.hamcrest.Matchers.containsString("Publicacion directa")));
    }

    @Test
    void rejectsContentThatIsNotApproved() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        GeneratedContent content = contentRepository.save(content(event.getId(), ContentStatus.PENDING_REVIEW, null));

        mockMvc.perform(post("/api/v1/publications/{id}/publish", content.getId()).with(adminJwt()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("only approved content can be published"));
    }


    @Test
    void listsAndGetsPublications() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        GeneratedContent content = contentRepository.save(content(event.getId(), ContentStatus.APPROVED, OffsetDateTime.now()));
        Publication publication = publicationRepository.save(Publication.pending(content.getId(), "TELEGRAM"));

        mockMvc.perform(get("/api/v1/publications").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(publication.getId()))
                .andExpect(jsonPath("$[0].contentId").value(content.getId()));

        mockMvc.perform(get("/api/v1/publications/{id}", publication.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publication.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }
    private GeneratedContent content(Long eventId, ContentStatus status, OffsetDateTime approvedAt) {
        OffsetDateTime generatedAt = approvedAt == null ? OffsetDateTime.now() : approvedAt.minusMinutes(1);
        return new GeneratedContent(null, eventId, 1L, "TELEGRAM", "INFORMATIVO", "Titulo", "Mensaje", status, generatedAt, approvedAt);
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Source(null, "Fuente Publication API", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new NewsArticle(null, sourceId, "Publicacion Telegram API", uniqueUrl("news"), "Resumen", "Contenido", uniqueHash(), now.minusHours(1), now, NewsStatus.EVENT_MATCHED, now, now);
    }

    private Event event(Long newsId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Event(null, "Evento publicable API", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(newsId), now, now, now, now);
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String uniqueHash() {
        String value = UUID.randomUUID().toString().replace("-", "");
        return value + value;
    }

    @TestConfiguration
    static class PublicationControllerTestConfiguration {

        @Bean
        @Primary
        @Order(0)
        PublishingProvider testPublishingProvider() {
            return new PublishingProvider() {
                @Override
                public boolean supports(String channel) {
                    return "TELEGRAM".equalsIgnoreCase(channel);
                }

                @Override
                public PublishingResult publish(PublishingRequest request) {
                    return new PublishingResult("message-test", "{\"ok\":true,\"messageId\":\"message-test\"}");
                }
            };
        }
    }
}
