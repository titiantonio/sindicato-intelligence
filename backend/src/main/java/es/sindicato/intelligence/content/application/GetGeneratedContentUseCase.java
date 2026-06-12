package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetGeneratedContentUseCase {

    private final GeneratedContentRepository contentRepository;

    public GetGeneratedContentUseCase(GeneratedContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional(readOnly = true)
    public GeneratedContent execute(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("generated content not found: " + id));
    }
}