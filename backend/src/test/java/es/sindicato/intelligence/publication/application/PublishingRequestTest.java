package es.sindicato.intelligence.publication.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublishingRequestTest {

    @Test
    void createsValidRequest() {
        PublishingRequest request = new PublishingRequest(10L, "TELEGRAM", "Titulo", "Mensaje");

        assertEquals(10L, request.contentId());
        assertEquals("TELEGRAM", request.channel());
        assertEquals("Titulo", request.title());
        assertEquals("Mensaje", request.message());
    }

    @Test
    void rejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class, () -> new PublishingRequest(10L, "TELEGRAM", "Titulo", " "));
    }
}
