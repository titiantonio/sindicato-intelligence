package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListPublicationsUseCase {

    private final PublicationRepository publicationRepository;

    public ListPublicationsUseCase(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @Transactional(readOnly = true)
    public List<Publication> execute() {
        return publicationRepository.findAll();
    }
}