package es.sindicato.intelligence.news.infrastructure;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaNewsRepository implements NewsRepository {

    private final EntityManager entityManager;

    public JpaNewsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public NewsArticle save(NewsArticle newsArticle) {
        NewsArticleEntity entity = toEntity(newsArticle);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<NewsArticle> findById(Long id) {
        return Optional.ofNullable(entityManager.find(NewsArticleEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public Optional<NewsArticle> findByUrl(String url) {
        return entityManager.createQuery(
                        "SELECT news FROM NewsArticleEntity news WHERE news.url = :url",
                        NewsArticleEntity.class
                )
                .setParameter("url", url)
                .getResultList()
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public Optional<NewsArticle> findByHash(String hash) {
        return entityManager.createQuery(
                        "SELECT news FROM NewsArticleEntity news WHERE news.hash = :hash",
                        NewsArticleEntity.class
                )
                .setParameter("hash", hash)
                .getResultList()
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<NewsArticle> findAll() {
        return entityManager.createQuery(
                        "SELECT news FROM NewsArticleEntity news ORDER BY news.capturedAt DESC, news.id DESC",
                        NewsArticleEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<NewsArticle> findByStatus(NewsStatus status, int limit) {
        return entityManager.createQuery(
                        "SELECT news FROM NewsArticleEntity news WHERE news.processingStatus = :status ORDER BY news.capturedAt ASC, news.id ASC",
                        NewsArticleEntity.class
                )
                .setParameter("status", status)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private NewsArticleEntity toEntity(NewsArticle newsArticle) {
        return new NewsArticleEntity(
                newsArticle.getId(),
                newsArticle.getSourceId(),
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                newsArticle.getHash(),
                newsArticle.getPublishedAt(),
                newsArticle.getCapturedAt(),
                newsArticle.getProcessingStatus(),
                newsArticle.getCreatedAt(),
                newsArticle.getUpdatedAt()
        );
    }

    private NewsArticle toDomain(NewsArticleEntity entity) {
        return new NewsArticle(
                entity.getId(),
                entity.getSourceId(),
                entity.getTitle(),
                entity.getUrl(),
                entity.getSummary(),
                entity.getContent(),
                entity.getHash(),
                entity.getPublishedAt(),
                entity.getCapturedAt(),
                entity.getProcessingStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
