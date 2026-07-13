package es.sindicato.intelligence.content.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.content.application.ContentGenerationContext;
import es.sindicato.intelligence.content.application.ContentAIProviderException;
import es.sindicato.intelligence.content.application.ContentAIRequest;
import es.sindicato.intelligence.content.application.ContentAIResponse;
import es.sindicato.intelligence.content.domain.ContentType;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiContentAIProviderTest {

    @Test
    void generatesContentFromGeminiJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiContentAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(method(POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("systemInstruction")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("responseSchema")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tu salida debe ser el objeto JSON final de contenido")))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "title": "Titulo Telegram",
                          "message": "Mensaje generado",
                          "hashtags": ["#Educacion", "#Andalucia"]
                        }
                        """), MediaType.APPLICATION_JSON));

        ContentAIResponse response = provider.generate(request());

        assertEquals("Titulo Telegram", response.title());
        assertEquals("Mensaje generado", response.message());
        assertEquals(List.of("#Educacion", "#Andalucia"), response.hashtags());
        server.verify();
    }

    @Test
    void rejectsMissingApiKeyClearly() {
        GeminiContentAIProvider provider = provider(RestClient.builder(), "");

        ContentAIProviderException exception = assertThrows(ContentAIProviderException.class, () -> provider.generate(request()));

        assertEquals("Gemini API key is required when app.ai.provider=gemini", exception.getMessage());
    }

    @Test
    void retriesInvalidJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiContentAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));

        ContentAIProviderException exception = assertThrows(ContentAIProviderException.class, () -> provider.generate(request()));

        assertEquals("Gemini response does not contain a JSON object", exception.getMessage());
        server.verify();
    }

    private GeminiContentAIProvider provider(RestClient.Builder builder, String apiKey) {
        return new GeminiContentAIProvider(builder, new ObjectMapper(), apiKey, "models/gemma-4-31b-it", 0.2, 1024);
    }

    private ContentAIRequest request() {
        return new ContentAIRequest(event(), analysis(), "TELEGRAM", "INFORMATIVO", ContentType.TELEGRAM_POST, "STANDARD", List.of(), new ContentGenerationContext(1, 0, null, false, List.of(), List.of()), "system", "EVENTO: Evento sindical");
    }

    private Event event() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(10L, "Evento sindical", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(2L), now, now, now, now);
    }

    private EventAIAnalysis analysis() {
        return new EventAIAnalysis(20L, 10L, "Resumen ejecutivo", "Resumen sindical", List.of("Punto clave"), List.of("Riesgo"), List.of("Oportunidad"), "deterministic-analysis", OffsetDateTime.parse("2026-06-08T10:00:00Z"));
    }

    private String geminiResponse(String text) {
        return """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": %s
                          }
                        ]
                      }
                    }
                  ]
                }
                """.formatted(toJsonString(text));
    }

    private String toJsonString(String value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
