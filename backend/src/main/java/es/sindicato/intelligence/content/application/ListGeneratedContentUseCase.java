package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListGeneratedContentUseCase {

    private final GeneratedContentRepository contentRepository;

    public ListGeneratedContentUseCase(GeneratedContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional(readOnly = true)
    public List<GeneratedContent> execute() {
        return contentRepository.findAll();
    }
}