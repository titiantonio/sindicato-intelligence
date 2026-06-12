package es.sindicato.intelligence.publication.domain;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository {

    Publication save(Publication publication);

    Optional<Publication> findById(Long id);

    List<Publication> findAll();

    List<Publication> findByContentId(Long contentId);
}