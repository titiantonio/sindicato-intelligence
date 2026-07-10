package es.sindicato.intelligence.classification.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.classification.application.AIProviderException;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class GeminiAIProviderTest {

    @Test
    void classifiesNewsFromGeminiJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAIProvider provider = new GeminiAIProvider(builder, new ObjectMapper(), properties("test-key"));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(method(POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SIPRI publica adjudicaciones")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("systemInstruction")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("responseSchema")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tu salida debe ser el objeto JSON final de clasificacion")))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "category": "SIPRI",
                          "subcategory": "Adjudicaciones",
                          "relevance": 95,
                          "impact": "HIGH",
                          "urgency": "HIGH",
                          "keywords": ["SIPRI", "adjudicaciones"],
                          "entities": ["Junta de Andalucia"],
                          "summary": "Resumen IA"
                        }
                        """), MediaType.APPLICATION_JSON));

        ClassificationAIResponse response = provider.classify(request());

        assertEquals(ClassificationCategory.SIPRI, response.category());
        assertEquals("Adjudicaciones", response.subcategory());
        assertEquals(BigDecimal.valueOf(95), response.relevance());
        assertEquals(ImpactLevel.HIGH, response.impact());
        assertEquals(UrgencyLevel.HIGH, response.urgency());
        assertEquals("SIPRI", response.keywords().getFirst());
        assertEquals("Junta de Andalucia", response.entities().getFirst());
        assertEquals("Resumen IA", response.summary());
        server.verify();
    }

    @Test
    void removesMarkdownFencesFromGeminiResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAIProvider provider = new GeminiAIProvider(builder, new ObjectMapper(), properties("test-key"));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("""
                        ```json
                        {
                          "category": "OPOSICIONES",
                          "subcategory": "Procesos selectivos",
                          "relevance": 90,
                          "impact": "HIGH",
                          "urgency": "MEDIUM",
                          "keywords": [],
                          "entities": [],
                          "summary": "Resumen"
                        }
                        ```
                        """), MediaType.APPLICATION_JSON));

        ClassificationAIResponse response = provider.classify(request());

        assertEquals(ClassificationCategory.OPOSICIONES, response.category());
        assertEquals(UrgencyLevel.MEDIUM, response.urgency());
        server.verify();
    }

    @Test
    void rejectsMissingApiKeyClearly() {
        GeminiAIProvider provider = new GeminiAIProvider(RestClient.builder(), new ObjectMapper(), properties(""));

        AIProviderException exception = assertThrows(AIProviderException.class, () -> provider.classify(request()));

        assertEquals("Gemini API key is required when app.ai.provider=gemini", exception.getMessage());
    }

    @Test
    void rejectsInvalidClassificationJsonClearly() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAIProvider provider = new GeminiAIProvider(builder, new ObjectMapper(), properties("test-key"));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));

        AIProviderException exception = assertThrows(AIProviderException.class, () -> provider.classify(request()));

        assertEquals("Gemini response does not contain a JSON object", exception.getMessage());
        server.verify();
    }

    @Test
    void retriesWhenGeminiResponseDoesNotContainText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAIProvider provider = new GeminiAIProvider(builder, new ObjectMapper(), properties("test-key"));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponseWithoutText(), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "category": "OTROS",
                          "subcategory": "INFORMACION_INSUFICIENTE",
                          "relevance": 0,
                          "impact": "LOW",
                          "urgency": "LOW"
                        }
                        """), MediaType.APPLICATION_JSON));

        ClassificationAIResponse response = provider.classify(request());

        assertEquals(ClassificationCategory.OTROS, response.category());
        assertEquals("INFORMACION_INSUFICIENTE", response.subcategory());
        assertEquals(BigDecimal.ZERO, response.relevance());
        assertEquals(java.util.List.of(), response.keywords());
        assertEquals(java.util.List.of(), response.entities());
        assertEquals("", response.summary());
        server.verify();
    }

    @Test
    void acceptsMinimalDiscardClassificationWithoutEnrichedFields() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAIProvider provider = new GeminiAIProvider(builder, new ObjectMapper(), properties("test-key"));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no devuelvas keywords, entities ni summary")))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "category": "OTROS",
                          "subcategory": "FUERA_DE_AMBITO",
                          "relevance": 0,
                          "impact": "LOW",
                          "urgency": "LOW"
                        }
                        """), MediaType.APPLICATION_JSON));

        ClassificationAIResponse response = provider.classify(request());

        assertEquals(ClassificationCategory.OTROS, response.category());
        assertEquals("FUERA_DE_AMBITO", response.subcategory());
        assertEquals(BigDecimal.ZERO, response.relevance());
        assertEquals(java.util.List.of(), response.keywords());
        assertEquals(java.util.List.of(), response.entities());
        assertEquals("", response.summary());
        server.verify();
    }

    private AiProviderProperties properties(String apiKey) {
        AiProviderProperties properties = new AiProviderProperties();
        properties.setProvider("gemini");
        properties.getGemini().setApiKey(apiKey);
        properties.getGemini().setModel("models/gemma-4-31b-it");
        properties.getGemini().setTemperature(0.2);
        properties.getGemini().setMaxOutputTokens(1024);
        return properties;
    }

    private ClassificationAIRequest request() {
        return new ClassificationAIRequest(
                "SIPRI publica adjudicaciones",
                "https://www.juntadeandalucia.es/educacion/sipri",
                "Resumen",
                "Contenido",
                "system",
                "TITULO: SIPRI publica adjudicaciones"
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

    private String geminiResponseWithoutText() {
        return """
                {
                  "candidates": [
                    {
                      "finishReason": "MAX_TOKENS",
                      "content": {
                        "parts": []
                      }
                    }
                  ]
                }
                """;
    }

    private String toJsonString(String value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
