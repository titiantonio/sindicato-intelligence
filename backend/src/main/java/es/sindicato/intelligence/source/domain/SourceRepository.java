package es.sindicato.intelligence.source.domain;

import java.util.List;
import java.util.Optional;

public interface SourceRepository {

    Source save(Source source);

    Optional<Source> findById(Long id);

    Optional<Source> findByUrl(String url);

    List<Source> findAll();
}
