package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListSourcesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListSourcesUseCase.class);

    private final SourceRepository sourceRepository;

    public ListSourcesUseCase(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public List<Source> execute() {
        List<Source> sources = sourceRepository.findAll();
        log.info("sources listed: count={}", sources.size());

        return sources;
    }
}
