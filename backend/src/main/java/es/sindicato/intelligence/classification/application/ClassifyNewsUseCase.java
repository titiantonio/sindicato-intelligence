package es.sindicato.intelligence.classification.application;

import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class ClassifyNewsUseCase {

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final ClassifyNewsPromptBuilder promptBuilder;
    private final AIProvider aiProvider;

    public ClassifyNewsUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            ClassifyNewsPromptBuilder promptBuilder,
            AIProvider aiProvider
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
    }

    @Transactional
    public NewsClassification execute(ClassifyNewsCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.newsId(), "newsId is required");

        NewsArticle newsArticle = newsRepository.findById(command.newsId())
                .orElseThrow(() -> new IllegalArgumentException("news not found: " + command.newsId()));

        if (classificationRepository.existsByNewsId(command.newsId())) {
            throw new IllegalArgumentException("news classification already exists");
        }

        ClassifyNewsPrompt prompt = promptBuilder.build(
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent()
        );
        ClassificationAIResponse aiResponse = aiProvider.classify(new ClassificationAIRequest(
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                prompt.systemPrompt(),
                prompt.userPrompt()
        ));

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

        return savedClassification;
    }
}
