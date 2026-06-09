package es.sindicato.intelligence.publication.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicationTest {

    @Test
    void createsPendingPublication() {
        Publication publication = Publication.pending(10L, "TELEGRAM");

        assertNull(publication.getId());
        assertEquals(10L, publication.getContentId());
        assertEquals("TELEGRAM", publication.getChannel());
        assertEquals(PublicationStatus.PENDING, publication.getStatus());
        assertNull(publication.getExternalId());
        assertNull(publication.getPublishedAt());
    }

    @Test
    void marksPublicationAsPublished() {
        Publication publication = Publication.pending(10L, "TELEGRAM");
        OffsetDateTime publishedAt = OffsetDateTime.parse("2026-06-09T10:00:00Z");

        publication.markPublished("message-123", publishedAt, "{\"ok\":true}");

        assertEquals(PublicationStatus.PUBLISHED, publication.getStatus());
        assertEquals("message-123", publication.getExternalId());
        assertEquals(publishedAt, publication.getPublishedAt());
        assertEquals("{\"ok\":true}", publication.getResponsePayload());
    }

    @Test
    void rejectsPublishedPublicationWithoutPublishedAt() {
        assertThrows(IllegalArgumentException.class, () -> new Publication(1L, 10L, "TELEGRAM", "message-123", PublicationStatus.PUBLISHED, null, null));
    }

    @Test
    void marksPublicationAsFailed() {
        Publication publication = Publication.pending(10L, "TELEGRAM");

        publication.markFailed("{\"ok\":false}");

        assertEquals(PublicationStatus.FAILED, publication.getStatus());
        assertNull(publication.getExternalId());
        assertNull(publication.getPublishedAt());
        assertEquals("{\"ok\":false}", publication.getResponsePayload());
    }
}
