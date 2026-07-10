package es.sindicato.intelligence.classification.application;

import java.util.Optional;

public interface NewsContentEnrichmentPort {

    Optional<String> enrich(String url);
}
