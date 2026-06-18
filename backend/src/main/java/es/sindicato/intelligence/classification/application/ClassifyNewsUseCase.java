package es.sindicato.intelligence.classification.application;

import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class ClassifyNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ClassifyNewsUseCase.class);

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final ClassifyNewsPromptBuilder promptBuilder;
    private final AIProvider aiProvider;
    private final AiOperationMetricsRecorder metricsRecorder;

    public ClassifyNewsUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            ClassifyNewsPromptBuilder promptBuilder,
            AIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
    }

    @Transactional
    public NewsClassification execute(ClassifyNewsCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.newsId(), "newsId is required");

        NewsArticle newsArticle = newsRepository.findById(command.newsId())
                .orElseThrow(() -> new IllegalArgumentException("news not found: " + command.newsId()));

        if (classificationRepository.existsByNewsId(command.newsId())) {
            log.warn("classification skipped because news already has classification: newsId={}", command.newsId());
            throw new IllegalArgumentException("news classification already exists");
        }

        log.info("classification started: newsId={}, title='{}'", newsArticle.getId(), abbreviate(newsArticle.getTitle()));

        ClassifyNewsPrompt prompt = promptBuilder.build(
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent()
        );
        ClassificationAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            aiResponse = aiProvider.classify(new ClassificationAIRequest(
                    newsArticle.getTitle(),
                    newsArticle.getSummary(),
                    newsArticle.getContent(),
                    prompt.systemPrompt(),
                    prompt.userPrompt()
            ));
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure("CLASSIFICATION", "WF02_CLASSIFICATION", providerName(), aiProvider.modelName(), "NEWS", newsArticle.getId(), startedAt, exception);
            log.error("classification failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
            throw exception;
        }
        metricsRecorder.recordSuccess("CLASSIFICATION", "WF02_CLASSIFICATION", providerName(), aiProvider.modelName(), "NEWS", newsArticle.getId(), startedAt);

        NewsClassification classification = new NewsClassification(
                null,
                newsArticle.getId(),
                aiResponse.category(),
                aiResponse.subcategory(),
                aiResponse.relevance(),
                aiResponse.impact(),
                aiResponse.urgency(),
                aiResponse.keywords(),
                aiResponse.entities(),
                OffsetDateTime.now()
        );

        NewsClassification savedClassification = classificationRepository.save(classification);
        newsArticle.markClassified();
        newsRepository.save(newsArticle);

        log.info(
                "classification completed: newsId={}, classificationId={}, category={}, subcategory='{}', relevance={}, impact={}, urgency={}",
                newsArticle.getId(),
                savedClassification.getId(),
                savedClassification.getCategory(),
                savedClassification.getSubcategory(),
                savedClassification.getRelevanceScore(),
                savedClassification.getImpactLevel(),
                savedClassification.getUrgencyLevel()
        );

        return savedClassification;
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

    private String providerName() {
        return aiProvider.getClass().getSimpleName();
    }
}
