package es.sindicato.intelligence.publication.domain;

import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;

public interface PublicationRepository {

    Publication save(Publication publication);

    Optional<Publication> findById(Long id);

    List<Publication> findAll();

    List<Publication> findScheduledBetween(OffsetDateTime fromInclusive, OffsetDateTime toExclusive);

    List<Publication> findByContentId(Long contentId);

    List<Publication> findDueScheduled(OffsetDateTime now, int limit);
}
