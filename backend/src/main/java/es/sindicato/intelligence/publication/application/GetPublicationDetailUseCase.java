package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPublicationDetailUseCase {

    private final PublicationRepository publicationRepository;
    private final GeneratedContentRepository contentRepository;
    private final GetEventDetailUseCase getEventDetailUseCase;

    public GetPublicationDetailUseCase(
            PublicationRepository publicationRepository,
            GeneratedContentRepository contentRepository,
            GetEventDetailUseCase getEventDetailUseCase
    ) {
        this.publicationRepository = publicationRepository;
        this.contentRepository = contentRepository;
        this.getEventDetailUseCase = getEventDetailUseCase;
    }

    @Transactional(readOnly = true)
    public PublicationDetail execute(Long publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new IllegalArgumentException("publication not found: " + publicationId));
        GeneratedContent content = contentRepository.findById(publication.getContentId())
                .orElseThrow(() -> new IllegalStateException("publication references missing content: " + publication.getContentId()));
        return new PublicationDetail(publication, content, getEventDetailUseCase.execute(content.getEventId()));
    }

    public record PublicationDetail(
            Publication publication,
            GeneratedContent content,
            GetEventDetailUseCase.EventDetail eventDetail
    ) {
    }
}
