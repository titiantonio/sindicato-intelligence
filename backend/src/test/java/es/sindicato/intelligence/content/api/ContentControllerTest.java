package es.sindicato.intelligence.content.api;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

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

    @Autowired
    private AiProviderSettingRepository providerSettingRepository;

    @Autowired
    private AiWorkflowSettingRepository workflowSettingRepository;

    @BeforeEach
    void configureDeterministicAi() {
        OffsetDateTime now = OffsetDateTime.now();
        AiProviderSetting provider = providerSettingRepository.findByCode("deterministic")
                .orElse(new AiProviderSetting("deterministic", "Deterministic", true, null, now, now));
        provider.update(true, null, false, now);
        providerSettingRepository.save(provider);

        AiWorkflowSetting workflow = workflowSettingRepository.findByWorkflowCode("WF05_CONTENT")
                .orElse(new AiWorkflowSetting("WF05_CONTENT", "deterministic", "deterministic-content", BigDecimal.ZERO, 1024, now, now));
        workflow.update("deterministic", "deterministic-content", BigDecimal.ZERO, 1024, now);
        workflowSettingRepository.save(workflow);
    }

    @Test
    void generatesApprovesAndRejectsContent() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        EventAIAnalysis analysis = analysisRepository.save(analysis(event.getId()));

        String response = mockMvc.perform(post("/api/v1/content/generate")
                        .with(adminJwt())
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

        mockMvc.perform(post("/api/v1/content/{id}/approve", contentId).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.approvedAt", notNullValue()));

        mockMvc.perform(post("/api/v1/content/{id}/reject", contentId).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }


    @Test
    void listsAndGetsGeneratedContent() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        Event event = eventRepository.save(event(newsArticle.getId()));
        GeneratedContent content = contentRepository.save(new GeneratedContent(null, event.getId(), 1L, "TELEGRAM", "INFORMATIVO", "Titulo", "Mensaje", ContentStatus.PENDING_REVIEW, OffsetDateTime.now(), null));

        mockMvc.perform(get("/api/v1/content").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(content.getId()))
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"));

        mockMvc.perform(get("/api/v1/content/{id}", content.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(content.getId()))
                .andExpect(jsonPath("$.eventId").value(event.getId()));
    }
    private EventAIAnalysis analysis(Long eventId) {
        return new EventAIAnalysis(null, eventId, "Resumen ejecutivo", "Resumen sindical", List.of("Punto clave"), List.of("Riesgo"), List.of("Oportunidad"), "deterministic-analysis", OffsetDateTime.now());
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
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
