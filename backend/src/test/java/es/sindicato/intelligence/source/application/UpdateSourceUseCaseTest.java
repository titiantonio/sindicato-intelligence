package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateSourceUseCaseTest {

    @Test
    void recordsAuditWhenSourceIsUpdated() {
        SourceRepository sourceRepository = mock(SourceRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        UpdateSourceUseCase useCase = new UpdateSourceUseCase(sourceRepository, audit);
        Source existing = source(1L, "BOJA", "https://old.example/rss", "RSS", 10, true);
        Source saved = source(1L, "BOJA actualizado", "https://new.example/rss", "RSS", 20, false);

        when(sourceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sourceRepository.findByUrl("https://new.example/rss")).thenReturn(Optional.empty());
        when(sourceRepository.save(any(Source.class))).thenReturn(saved);

        Source result = useCase.execute(1L, new UpdateSourceCommand("BOJA actualizado", "https://new.example/rss", "RSS", 20, false));

        assertEquals(saved, result);
        verify(audit).record(eq("SOURCE_UPDATED"), eq("SOURCE"), eq(1L), any(), any());
    }

    private Source source(Long id, String name, String url, String type, int priority, boolean active) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return new Source(id, name, url, type, priority, active, now, now);
    }
}
