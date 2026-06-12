package es.sindicato.intelligence.content.domain;

import java.util.List;
import java.util.Optional;

public interface GeneratedContentRepository {

    GeneratedContent save(GeneratedContent content);

    Optional<GeneratedContent> findById(Long id);

    List<GeneratedContent> findAll();

    List<GeneratedContent> findByEventId(Long eventId);
}