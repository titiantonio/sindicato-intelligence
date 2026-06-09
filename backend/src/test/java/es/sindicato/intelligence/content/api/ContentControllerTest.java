package es.sindicato.intelligence.content.api;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventAIAnalysisRepository analysisRepository;

    @Autowired
    private GeneratedContentRepository contentRepository;

    @Test
    void generatesApprovesAndRejectsContent() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        EventAIAnalysis analysis = analysisRepository.save(analysis(event.getId()));

        String response = mockMvc.perform(post("/api/v1/content/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": %d,
                                  "analysisId": %d,
                                  "channel": "TELEGRAM",
                                  "tone": "INFORMATIVO",
                                  "length": "STANDARD"
                                }
                                """.formatted(event.getId(), analysis.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.eventId").value(event.getId()))
                .andExpect(jsonPath("$.createdBy").value(1))
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long contentId = contentRepository.findByEventId(event.getId()).getFirst().getId();

        mockMvc.perform(post("/api/v1/content/{id}/approve", contentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approvedAt", notNullValue()));

        mockMvc.perform(post("/api/v1/content/{id}/reject", contentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    private EventAIAnalysis analysis(Long eventId) {
        return new EventAIAnalysis(null, eventId, "Resumen ejecutivo", "Resumen sindical", List.of("Punto clave"), List.of("Riesgo"), List.of("Oportunidad"), "deterministic-analysis", OffsetDateTime.now());
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new Source(null, "Fuente Content API", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return new NewsArticle(null, sourceId, "CCOO mantiene movilizaciones 0-3", uniqueUrl("news"), "Resumen", "Contenido", uniqueHash(), now.minusHours(1), now, NewsStatus.EVENT_MATCHED, now, now);
    }

    private Event event(Long newsId) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
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
