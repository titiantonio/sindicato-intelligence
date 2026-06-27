package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetGeneratedContentDetailUseCase {

    private final GeneratedContentRepository contentRepository;
    private final GetEventDetailUseCase getEventDetailUseCase;

    public GetGeneratedContentDetailUseCase(
            GeneratedContentRepository contentRepository,
            GetEventDetailUseCase getEventDetailUseCase
    ) {
        this.contentRepository = contentRepository;
        this.getEventDetailUseCase = getEventDetailUseCase;
    }

    @Transactional(readOnly = true)
    public GeneratedContentDetail execute(Long contentId) {
        GeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + contentId));
        return new GeneratedContentDetail(content, getEventDetailUseCase.execute(content.getEventId()));
    }

    public record GeneratedContentDetail(
            GeneratedContent content,
            GetEventDetailUseCase.EventDetail eventDetail
    ) {
    }
}
