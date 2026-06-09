package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.application.PublishingRequest;
import es.sindicato.intelligence.publication.application.PublishingResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TelegramPublisherTest {

    @Test
    void supportsTelegramChannel() {
        TelegramPublisher publisher = publisher(RestClient.builder(), "token", "chat-id");

        assertTrue(publisher.supports("telegram"));
    }

    @Test
    void publishesMessageAndReturnsTelegramMessageId() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        TelegramPublisher publisher = publisher(builder, "test-token", "chat-id");
        server.expect(requestTo("https://api.telegram.org/bottest-token/sendMessage"))
                .andExpect(method(POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("chat-id")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Titulo")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mensaje")))
                .andRespond(withSuccess("""
                        {
                          "ok": true,
                          "result": {
                            "message_id": 123
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        PublishingResult result = publisher.publish(new PublishingRequest(10L, "TELEGRAM", "Titulo", "Mensaje"));

        assertEquals("123", result.externalId());
        assertTrue(result.responsePayload().contains("\"ok\":true"));
        assertTrue(result.responsePayload().contains("\"messageId\":\"123\""));
        server.verify();
    }

    @Test
    void rejectsMissingChatId() {
        TelegramPublisher publisher = publisher(RestClient.builder(), "token", "");

        PublishingProviderException exception = assertThrows(PublishingProviderException.class, () -> publisher.publish(new PublishingRequest(10L, "TELEGRAM", "Titulo", "Mensaje")));

        assertEquals("Telegram chat id is required", exception.getMessage());
    }

    @Test
    void wrapsTelegramHttpError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        TelegramPublisher publisher = publisher(builder, "test-token", "chat-id");
        server.expect(requestTo("https://api.telegram.org/bottest-token/sendMessage"))
                .andRespond(withBadRequest().body("{\"ok\":false,\"description\":\"Bad Request\"}").contentType(MediaType.APPLICATION_JSON));

        PublishingProviderException exception = assertThrows(PublishingProviderException.class, () -> publisher.publish(new PublishingRequest(10L, "TELEGRAM", "Titulo", "Mensaje")));

        assertTrue(exception.getMessage().contains("Telegram publication failed"));
        server.verify();
    }

    private TelegramPublisher publisher(RestClient.Builder builder, String botToken, String chatId) {
        return new TelegramPublisher(builder, new ObjectMapper(), "https://api.telegram.org", botToken, chatId);
    }
}
