package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CreateNewsUseCase {

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

        sourceRepository.findById(command.sourceId()).orElseThrow(() ->
                new IllegalArgumentException("source not found: " + command.sourceId())
        );

        newsRepository.findByUrl(command.url()).ifPresent(newsArticle -> {
            throw new IllegalArgumentException("news url already exists");
        });

        String hash = newsHashGenerator.calculate(command);
        newsRepository.findByHash(hash).ifPresent(newsArticle -> {
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

        return newsRepository.save(newsArticle);
    }
}
