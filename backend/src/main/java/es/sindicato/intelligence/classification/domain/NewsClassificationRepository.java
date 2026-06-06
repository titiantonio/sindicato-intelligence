package es.sindicato.intelligence.classification.domain;

import java.util.Optional;

public interface NewsClassificationRepository {

    NewsClassification save(NewsClassification classification);

    Optional<NewsClassification> findById(Long id);

    Optional<NewsClassification> findByNewsId(Long newsId);

    boolean existsByNewsId(Long newsId);
}
