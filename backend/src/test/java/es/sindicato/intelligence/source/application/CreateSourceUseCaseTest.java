package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateSourceUseCaseTest {

    @Test
    void createsSourceWhenUrlDoesNotExist() {
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateSourceUseCase useCase = new CreateSourceUseCase(sourceRepository);
        CreateSourceCommand command = new CreateSourceCommand(
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true
        );
        Source savedSource = source(
                1L,
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true
        );
        when(sourceRepository.findByUrl(command.url())).thenReturn(Optional.empty());
        when(sourceRepository.save(any(Source.class))).thenReturn(savedSource);

        Source result = useCase.execute(command);

        ArgumentCaptor<Source> sourceCaptor = ArgumentCaptor.forClass(Source.class);
        verify(sourceRepository).save(sourceCaptor.capture());
        Source sourceToSave = sourceCaptor.getValue();

        assertEquals(savedSource, result);
        assertEquals("BOJA", sourceToSave.getName());
        assertEquals("https://www.juntadeandalucia.es/boja", sourceToSave.getUrl());
        assertEquals("RSS", sourceToSave.getType());
        assertEquals(10, sourceToSave.getPriority());
        assertTrue(sourceToSave.isActive());
        assertNotNull(sourceToSave.getCreatedAt());
        assertNotNull(sourceToSave.getUpdatedAt());
    }

    @Test
    void rejectsDuplicatedUrl() {
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateSourceUseCase useCase = new CreateSourceUseCase(sourceRepository);
        CreateSourceCommand command = new CreateSourceCommand(
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true
        );
        when(sourceRepository.findByUrl(command.url())).thenReturn(Optional.of(source(
                1L,
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true
        )));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        verify(sourceRepository, never()).save(any(Source.class));
    }

    @Test
    void rejectsNullCommand() {
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateSourceUseCase useCase = new CreateSourceUseCase(sourceRepository);

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    private Source source(Long id, String name, String url, String type, int priority, boolean active) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new Source(
                id,
                name,
                url,
                type,
                priority,
                active,
                now,
                now
        );
    }
}
