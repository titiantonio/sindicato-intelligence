package es.sindicato.intelligence.publication.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublishingResultTest {

    @Test
    void createsValidResult() {
        PublishingResult result = new PublishingResult("message-123", "{\"ok\":true}");

        assertEquals("message-123", result.externalId());
        assertEquals("{\"ok\":true}", result.responsePayload());
    }

    @Test
    void rejectsBlankExternalId() {
        assertThrows(IllegalArgumentException.class, () -> new PublishingResult(" ", null));
    }
}
