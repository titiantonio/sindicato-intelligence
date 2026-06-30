package es.sindicato.intelligence.publication.domain;

import java.util.List;

public interface PublicationTargetRepository {

    PublicationTarget save(PublicationTarget target);

    List<PublicationTarget> findByPublicationId(Long publicationId);
}
