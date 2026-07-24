package es.sindicato.intelligence.analysis.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.analysis.application.AnalysisAIProviderException;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import es.sindicato.intelligence.analysis.application.AnalysisNewsItem;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiAnalysisAIProviderTest {

    @Test
    void generatesAnalysisFromGeminiJsonResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(method(POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("systemInstruction")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("responseSchema")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"temperature\":0.1")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"topP\":0.2")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"topK\":1")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"candidateCount\":1")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"maxOutputTokens\":2048")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tu salida debe ser el objeto JSON final de analisis")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Responde en espanol y no repitas palabras o fragmentos")))
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
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess(geminiResponse("sin json"), MediaType.APPLICATION_JSON));

        AnalysisAIProviderException exception = assertThrows(AnalysisAIProviderException.class, () -> provider.generate(request()));

        assertEquals("Gemini response does not contain a JSON object", exception.getMessage());
        server.verify();
    }

    @Test
    void retriesRecitationWithNewsContentStripped() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("contenido: texto largo de noticia")))
                .andRespond(withSuccess(geminiRecitationResponse(), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("contenido: texto largo de noticia"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("tema_operativo")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bolsa Unica Comun")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("El proveedor ha bloqueado el intento anterior por RECITATION")))
                .andRespond(withSuccess(geminiResponse("""
                        {
                          "executiveSummary": "Resumen ejecutivo",
                          "unionSummary": "Resumen sindical",
                          "keyPoints": ["Punto clave"],
                          "risks": [],
                          "opportunities": [],
                          "affectedGroups": [],
                          "recommendedMonitoring": ["Fuente oficial"]
                        }
                        """), MediaType.APPLICATION_JSON));

        AnalysisAIResponse response = provider.generate(requestWithNewsContent());

        assertEquals("Resumen ejecutivo", response.executiveSummary());
        assertEquals(List.of("Fuente oficial"), response.recommendedMonitoring());
        server.verify();
    }

    @Test
    void fallsBackConservativelyWhenRecitationRetryStillHasInvalidJson() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andRespond(withSuccess(geminiRecitationResponse(), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andRespond(withSuccess(geminiResponse("{\"executiveSummary\":\"texto incompleto\""), MediaType.APPLICATION_JSON));

        AnalysisAIResponse response = provider.generate(requestWithNewsContent());

        assertEquals("models/gemma-4-31b-it:conservative-recitation-fallback", response.modelUsed());
        assertEquals(List.of("Revisar el PDF oficial enlazado y confirmar plazos, colectivos y requisitos antes de publicar."), response.recommendedMonitoring());
        server.verify();
    }

    @Test
    void fallsBackConservativelyWhenRetryStillHasIncompleteJson() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAnalysisAIProvider provider = provider(builder, "test-key");
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andRespond(withSuccess(geminiResponse("{\"executiveSummary\":\"Publicacion de calificaciones\""), MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent"))
                .andRespond(withSuccess(geminiResponse("{\"executiveSummary\":\"Publicacion de calificaciones\",\"unionSummary\":\"texto repetido own own own\""), MediaType.APPLICATION_JSON));

        AnalysisAIResponse response = provider.generate(oposicionesRequest());

        assertEquals("models/gemma-4-31b-it:conservative-json-fallback", response.modelUsed());
        assertEquals(List.of("Aspirantes a procesos selectivos"), response.affectedGroups());
        assertEquals(List.of("Revisar la fuente enlazada y confirmar detalles operativos antes de publicar."), response.recommendedMonitoring());
        server.verify();
    }

    private GeminiAnalysisAIProvider provider(RestClient.Builder builder, String apiKey) {
        return new GeminiAnalysisAIProvider(builder, new ObjectMapper(), apiKey, "models/gemma-4-31b-it", 0.2, 1024);
    }

    private AnalysisAIRequest request() {
        return new AnalysisAIRequest(
                10L,
                "Resolución de 22 de julio de 2026, por la que se amplía el plazo de solicitudes para actualizar la Bolsa Única Común.",
                "Descripcion",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(),
                "system",
                "EVENTO: Evento sindical"
        );
    }

    private AnalysisAIRequest requestWithNewsContent() {
        return new AnalysisAIRequest(
                10L,
                "Resolución de 22 de julio de 2026, por la que se amplía el plazo de solicitudes para actualizar la Bolsa Única Común.",
                "Descripcion",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(new AnalysisNewsItem(
                        44L,
                        "UGT Enseñanza",
                        4,
                        "Resolución de 22 de julio de 2026, por la que se amplía el plazo de solicitudes para actualizar la Bolsa Única Común.",
                        "http://www.juntadeandalucia.es/boja/2026/214001/BOJA26-214001-00002-9998-01_00341229.pdf",
                        "Boletín: BOJA Extraordinario nº 213501 de 2026",
                        "",
                        OffsetDateTime.parse("2026-07-24T10:00:00Z")
                )),
                "system",
                """
                        EVENTO: Evento sindical

                        NOTICIAS:
                        - id: 1
                          titulo: Noticia
                          contenido: texto largo de noticia
                          publicado: 2026-07-24T10:00:00Z
                        """
        );
    }

    private AnalysisAIRequest oposicionesRequest() {
        return new AnalysisAIRequest(
                25L,
                "Oposiciones docentes 2026 en Andalucia: calificaciones de la segunda prueba de ingreso y de la prueba de acceso",
                "Publicadas en los portales de las personas opositoras",
                EventCategory.OPOSICIONES,
                Importance.CRITICAL,
                List.of(new AnalysisNewsItem(
                        221L,
                        "ANPE Andalucia",
                        6,
                        "Oposiciones docentes 2026 en Andalucia: calificaciones de la segunda prueba de ingreso y de la prueba de acceso",
                        "https://anpeandalucia.es/notices/204873/Oposiciones-docentes-2026-en-Andalucia-calificaciones-de-la-segunda-prueba-de-ingreso-y-de-la-prueba-de-acceso",
                        "Publicadas en los portales de las personas opositoras",
                        "Publicadas en los portales de las personas opositoras",
                        OffsetDateTime.parse("2026-07-24T10:00:00Z")
                )),
                "system",
                "EVENTO: Oposiciones docentes 2026"
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

    private String geminiRecitationResponse() {
        return """
                {
                  "candidates": [
                    {
                      "finishReason": "RECITATION"
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
