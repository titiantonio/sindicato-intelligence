package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPublicationUseCase {

    private final PublicationRepository publicationRepository;

    public GetPublicationUseCase(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Transactional(readOnly = true)
    public Publication execute(Long id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publication not found: " + id));
    }
}