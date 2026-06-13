package es.sindicato.intelligence.source.api;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    void createsSource() throws Exception {
        String url = uniqueUrl();

        mockMvc.perform(post("/api/v1/sources").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Fuente API",
                                  "url": "%s",
                                  "type": "RSS",
                                  "priority": 10,
                                  "active": true
                                }
                                """.formatted(url)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Fuente API"))
                .andExpect(jsonPath("$.url").value(url))
                .andExpect(jsonPath("$.type").value("RSS"))
                .andExpect(jsonPath("$.priority").value(10))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void listsSources() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl()));

        mockMvc.perform(get("/api/v1/sources").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(source.getId().intValue())))
                .andExpect(jsonPath("$[*].name", hasItem("Fuente API")));
    }

    @Test
    void updatesSource() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl()));
        String updatedUrl = uniqueUrl();

        mockMvc.perform(put("/api/v1/sources/{id}", source.getId()).with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Fuente Actualizada",
                                  "url": "%s",
                                  "type": "WEB",
                                  "priority": 20,
                                  "active": false
                                }
                                """.formatted(updatedUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(source.getId()))
                .andExpect(jsonPath("$.name").value("Fuente Actualizada"))
                .andExpect(jsonPath("$.url").value(updatedUrl))
                .andExpect(jsonPath("$.type").value("WEB"))
                .andExpect(jsonPath("$.priority").value(20))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/sources").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "url": "not-a-url",
                                  "type": "",
                                  "priority": -1,
                                  "active": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingSource() throws Exception {
        mockMvc.perform(put("/api/v1/sources/{id}", 999999L).with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Fuente No Existe",
                                  "url": "https://test.example/missing-source",
                                  "type": "RSS",
                                  "priority": 10,
                                  "active": true
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    private Source source(String url) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);

        return new Source(
                null,
                "Fuente API",
                url,
                "RSS",
                10,
                true,
                now,
                now
        );
    }

    private String uniqueUrl() {
        return "https://test.example/sources/" + UUID.randomUUID();
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }}
