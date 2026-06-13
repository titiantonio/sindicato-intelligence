package es.sindicato.intelligence.content.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneratedContentTest {

    @Test
    void createsGeneratedContent() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");

        GeneratedContent content = content(ContentStatus.PENDING_REVIEW, generatedAt, null);

        assertEquals(1L, content.getId());
        assertEquals(10L, content.getEventId());
        assertEquals(1L, content.getCreatedBy());
        assertEquals("TELEGRAM", content.getChannel());
        assertEquals("INFORMATIVO", content.getTone());
        assertEquals("Titulo", content.getTitle());
        assertEquals("Mensaje", content.getContent());
        assertEquals(ContentStatus.PENDING_REVIEW, content.getStatus());
        assertEquals(generatedAt, content.getGeneratedAt());
        assertNull(content.getApprovedAt());
    }

    @Test
    void rejectsMissingTitle() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new GeneratedContent(
                1L,
                10L,
                1L,
                "TELEGRAM",
                "INFORMATIVO",
                " ",
                "Mensaje",
                ContentStatus.PENDING_REVIEW,
                generatedAt,
                null
        ));
    }

    @Test
    void approvesContent() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        OffsetDateTime approvedAt = OffsetDateTime.parse("2026-06-08T11:00:00Z");
        GeneratedContent content = content(ContentStatus.PENDING_REVIEW, generatedAt, null);

        content.approve(approvedAt);

        assertEquals(ContentStatus.APPROVED, content.getStatus());
        assertEquals(approvedAt, content.getApprovedAt());
    }

    @Test
    void rejectsApprovalBeforeGeneration() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        GeneratedContent content = content(ContentStatus.PENDING_REVIEW, generatedAt, null);

        assertThrows(IllegalArgumentException.class, () -> content.approve(generatedAt.minusMinutes(1)));
    }

    @Test
    void rejectsContentAndClearsApproval() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        GeneratedContent content = content(ContentStatus.APPROVED, generatedAt, generatedAt.plusHours(1));

        content.reject();

        assertEquals(ContentStatus.REJECTED, content.getStatus());
        assertNull(content.getApprovedAt());
    }

    @Test
    void onlyApprovedContentCanBePublished() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        GeneratedContent content = content(ContentStatus.PENDING_REVIEW, generatedAt, null);

        assertThrows(IllegalStateException.class, content::markPublished);
    }

    @Test
    void editsContentAndReturnsItToPendingReview() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        GeneratedContent content = content(ContentStatus.APPROVED, generatedAt, generatedAt.plusHours(1));

        content.edit("Nuevo titulo", "Nuevo mensaje", "URGENTE");

        assertEquals("Nuevo titulo", content.getTitle());
        assertEquals("Nuevo mensaje", content.getContent());
        assertEquals("URGENTE", content.getTone());
        assertEquals(ContentStatus.PENDING_REVIEW, content.getStatus());
        assertNull(content.getApprovedAt());
    }

    @Test
    void rejectsEditingPublishedContent() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        GeneratedContent content = content(ContentStatus.APPROVED, generatedAt, generatedAt.plusHours(1));
        content.markPublished();

        assertThrows(IllegalStateException.class, () -> content.edit("Nuevo titulo", "Nuevo mensaje", "URGENTE"));
    }

    private GeneratedContent content(ContentStatus status, OffsetDateTime generatedAt, OffsetDateTime approvedAt) {
        return new GeneratedContent(
                1L,
                10L,
                1L,
                "TELEGRAM",
                "INFORMATIVO",
                "Titulo",
                "Mensaje",
                status,
                generatedAt,
                approvedAt
        );
    }
}
