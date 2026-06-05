package es.sindicato.intelligence.source.infrastructure;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaSourceRepositoryTest {

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    void savesAndFindsSourceById() {
        Source source = source("https://test.example/source-by-id");

        Source saved = sourceRepository.save(source);
        Optional<Source> found = sourceRepository.findById(saved.getId());

        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Fuente Test", found.get().getName());
        assertEquals("RSS", found.get().getType());
    }

    @Test
    void findsSourceByUrl() {
        Source source = source("https://test.example/source-by-url");

        sourceRepository.save(source);
        Optional<Source> found = sourceRepository.findByUrl("https://test.example/source-by-url");

        assertTrue(found.isPresent());
        assertEquals("https://test.example/source-by-url", found.get().getUrl());
    }

    @Test
    void listsSavedSources() {
        Source first = source("https://test.example/source-list-1");
        Source second = source("https://test.example/source-list-2");

        Source savedFirst = sourceRepository.save(first);
        Source savedSecond = sourceRepository.save(second);
        List<Source> sources = sourceRepository.findAll();

        assertTrue(sources.stream().anyMatch(source -> source.getId().equals(savedFirst.getId())));
        assertTrue(sources.stream().anyMatch(source -> source.getId().equals(savedSecond.getId())));
    }

    private Source source(String url) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new Source(
                null,
                "Fuente Test",
                url,
                "RSS",
                50,
                true,
                now,
                now
        );
    }
}
