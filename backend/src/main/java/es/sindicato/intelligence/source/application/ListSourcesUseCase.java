package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListSourcesUseCase {

    private final SourceRepository sourceRepository;

    public ListSourcesUseCase(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public List<Source> execute() {
        return sourceRepository.findAll();
    }
}
