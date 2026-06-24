package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.application.AiModelOption;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

class GeminiAiProviderModelClientTest {

    @Test
    void listsOnlyGenerateContentModelsUsingApiKeyHeader() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAiProviderModelClient client = new GeminiAiProviderModelClient(builder);
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models?pageSize=1000"))
                .andExpect(method(GET))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andRespond(withSuccess("""
                        {
                          "models": [
                            {
                              "name": "models/gemini-2.5-flash",
                              "displayName": "Gemini 2.5 Flash",
                              "supportedGenerationMethods": ["generateContent"]
                            },
                            {
                              "name": "models/text-embedding",
                              "displayName": "Embedding",
                              "supportedGenerationMethods": ["embedContent"]
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        List<AiModelOption> models = client.listModels("test-key");

        assertEquals(1, models.size());
        assertEquals("models/gemini-2.5-flash", models.getFirst().name());
        server.verify();
    }

    @Test
    void failsClearlyWhenApiKeyIsInvalid() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiAiProviderModelClient client = new GeminiAiProviderModelClient(builder);
        server.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models?pageSize=1000"))
                .andRespond(withUnauthorizedRequest());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> client.listModels("bad-key"));

        assertEquals("Gemini models request failed with HTTP 401", exception.getMessage());
        server.verify();
    }
}
