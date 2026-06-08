package es.sindicato.intelligence.analysis.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.analysis.application.AnalysisAIProviderException;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiAnalysisAIProviderTest {

    @Test
    void generatesAnalysisFromGeminiJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent?key=test-key"))
                .andExpect(method(POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("systemInstruction")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("responseSchema")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tu salida debe ser el objeto JSON final de analisis")))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "executiveSummary": "Resumen ejecutivo",
                          "unionSummary": "Resumen sindical",
                          "keyPoints": ["Punto clave"],
                          "risks": ["Riesgo"],
                          "opportunities": ["Oportunidad"],
                          "affectedGroups": ["Docentes"],
                          "recommendedMonitoring": ["BOJA"]
                        }
                        """), MediaType.APPLICATION_JSON));

        AnalysisAIResponse response = provider.generate(request());

        assertEquals("Resumen ejecutivo", response.executiveSummary());
        assertEquals("Resumen sindical", response.unionSummary());
        assertEquals(List.of("Punto clave"), response.keyPoints());
        assertEquals(List.of("Riesgo"), response.risks());
        assertEquals(List.of("Oportunidad"), response.opportunities());
        assertEquals("models/gemma-4-31b-it", response.modelUsed());
        server.verify();
    }

    @Test
    void rejectsMissingApiKeyClearly() {
        GeminiAnalysisAIProvider provider = provider(RestClient.builder(), "");

        AnalysisAIProviderException exception = assertThrows(AnalysisAIProviderException.class, () -> provider.generate(request()));

        assertEquals("Gemini API key is required when app.ai.provider=gemini", exception.getMessage());
    }

    @Test
    void retriesInvalidJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent?key=test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent?key=test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));

        AnalysisAIProviderException exception = assertThrows(AnalysisAIProviderException.class, () -> provider.generate(request()));

        assertEquals("Gemini response does not contain a JSON object", exception.getMessage());
        server.verify();
    }

    private GeminiAnalysisAIProvider provider(RestClient.Builder builder, String apiKey) {
        return new GeminiAnalysisAIProvider(builder, new ObjectMapper(), apiKey, "models/gemma-4-31b-it", 0.2, 1024);
    }

    private AnalysisAIRequest request() {
        return new AnalysisAIRequest(
                10L,
                "Evento sindical",
                "Descripcion",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(),
                "system",
                "EVENTO: Evento sindical"
        );
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
