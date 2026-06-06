package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.Objects;

@Service
public class CreateNewsUseCase {

    private final NewsRepository newsRepository;
    private final SourceRepository sourceRepository;

    public CreateNewsUseCase(NewsRepository newsRepository, SourceRepository sourceRepository) {
        this.newsRepository = newsRepository;
        this.sourceRepository = sourceRepository;
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

        String hash = calculateHash(command);
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

    private String calculateHash(CreateNewsCommand command) {
        String textForHash = hasText(command.content()) ? command.content() : command.summary();
        String publishedAt = command.publishedAt() == null ? "" : command.publishedAt().toString();
        String rawHash = normalize(command.title()) + "|" + normalize(textForHash) + "|" + publishedAt;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawHash.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm not available", exception);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
