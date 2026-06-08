package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CreateNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateNewsUseCase.class);

    private final NewsRepository newsRepository;
    private final SourceRepository sourceRepository;
    private final NewsHashGenerator newsHashGenerator;

    public CreateNewsUseCase(
            NewsRepository newsRepository,
            SourceRepository sourceRepository,
            NewsHashGenerator newsHashGenerator
    ) {
        this.newsRepository = newsRepository;
        this.sourceRepository = sourceRepository;
        this.newsHashGenerator = newsHashGenerator;
    }

    @Transactional
    public NewsArticle execute(CreateNewsCommand command) {
        Objects.requireNonNull(command, "command is required");

        log.info("news creation started: sourceId={}, title='{}', url='{}'", command.sourceId(), abbreviate(command.title()), command.url());

        sourceRepository.findById(command.sourceId()).orElseThrow(() -> {
            log.warn("news creation skipped because source does not exist: sourceId={}, url='{}'", command.sourceId(), command.url());
            return new IllegalArgumentException("source not found: " + command.sourceId());
        });

        newsRepository.findByUrl(command.url()).ifPresent(newsArticle -> {
            log.warn("news creation skipped because url already exists: existingNewsId={}, url='{}'", newsArticle.getId(), command.url());
            throw new IllegalArgumentException("news url already exists");
        });

        String hash = newsHashGenerator.calculate(command);
        newsRepository.findByHash(hash).ifPresent(newsArticle -> {
            log.warn("news creation skipped because hash already exists: existingNewsId={}, url='{}'", newsArticle.getId(), command.url());
            throw new IllegalArgumentException("news hash already exists");
        });

        OffsetDateTime now = OffsetDateTime.now();
        NewsArticle newsArticle = new NewsArticle(
                null,
                command.sourceId(),
                command.title(),
                command.url(),
                command.summary(),
                command.content(),
                hash,
                command.publishedAt(),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );

        NewsArticle savedNews = newsRepository.save(newsArticle);
        log.info("news creation completed: newsId={}, sourceId={}, status={}", savedNews.getId(), savedNews.getSourceId(), savedNews.getProcessingStatus());

        return savedNews;
    }

    private String abbreviate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmed = value.replaceAll("\\s+", " ").trim();
        if (trimmed.length() <= 120) {
            return trimmed;
        }

        return trimmed.substring(0, 117) + "...";
    }
}
