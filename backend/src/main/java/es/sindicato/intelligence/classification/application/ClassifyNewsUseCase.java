package es.sindicato.intelligence.classification.application;

import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ClassifyNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ClassifyNewsUseCase.class);

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final ClassifyNewsPromptBuilder promptBuilder;
    private final AIProvider aiProvider;
    private final AiOperationMetricsRecorder metricsRecorder;
    private final AiModelExecutionCoordinator aiModelExecutionCoordinator;
    private final NewsContentEnrichmentPort newsContentEnrichmentPort;

    public ClassifyNewsUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            ClassifyNewsPromptBuilder promptBuilder,
            AIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder,
            AiModelExecutionCoordinator aiModelExecutionCoordinator,
            NewsContentEnrichmentPort newsContentEnrichmentPort
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
        this.newsContentEnrichmentPort = newsContentEnrichmentPort;
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

        String effectiveContent = enrichContentIfNeeded(newsArticle);
        ClassifyNewsPrompt prompt = promptBuilder.build(
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                effectiveContent
        );
        ClassificationAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            aiResponse = aiModelExecutionCoordinator.execute("WF02_CLASSIFICATION", () -> aiProvider.classify(new ClassificationAIRequest(
                            newsArticle.getTitle(),
                            newsArticle.getUrl(),
                            newsArticle.getSummary(),
                            effectiveContent,
                            prompt.systemPrompt(),
                            prompt.userPrompt()
                    )));
        } catch (RuntimeException exception) {
            aiResponse = fallbackForOutOfScopeResponseWithoutText(newsArticle, exception);
            if (aiResponse != null) {
                log.warn("classification used out-of-scope fallback after provider response without text: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            } else {
                metricsRecorder.recordFailure("CLASSIFICATION", "WF02_CLASSIFICATION", aiProvider.providerName(), aiProvider.modelName(), "NEWS", newsArticle.getId(), startedAt, exception);
                log.error("classification failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
                throw exception;
            }
        }
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
        if (classification.isDiscardableForEventDetection()) {
            newsArticle.markDiscarded();
            log.warn(
                    "classification discarded news outside event scope: newsId={}, category={}, subcategory='{}', relevance={}",
                    newsArticle.getId(),
                    classification.getCategory(),
                    classification.getSubcategory(),
                    classification.getRelevanceScore()
            );
        } else {
            newsArticle.markClassified();
        }
        newsRepository.save(newsArticle);

        metricsRecorder.recordSuccess(
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                aiProvider.providerName(),
                aiProvider.modelName(),
                "NEWS",
                newsArticle.getId(),
                startedAt,
                classificationDetails(newsArticle, savedClassification, aiResponse.summary())
        );

        log.info(
                "classification completed: newsId={}, classificationId={}, status={}, category={}, subcategory='{}', relevance={}, impact={}, urgency={}",
                newsArticle.getId(),
                savedClassification.getId(),
                newsArticle.getProcessingStatus(),
                savedClassification.getCategory(),
                savedClassification.getSubcategory(),
                savedClassification.getRelevanceScore(),
                savedClassification.getImpactLevel(),
                savedClassification.getUrgencyLevel()
        );

        return savedClassification;
    }

    private ClassificationAIResponse fallbackForOutOfScopeResponseWithoutText(NewsArticle newsArticle, RuntimeException exception) {
        if (!isProviderResponseWithoutText(exception) || containsEducationScopeSignal(newsArticle)) {
            return null;
        }

        return new ClassificationAIResponse(
                ClassificationCategory.OTROS,
                "FUERA_DE_AMBITO",
                BigDecimal.ZERO,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                null
        );
    }

    private boolean isProviderResponseWithoutText(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && message.startsWith("Gemini response does not contain candidates[0].content.parts[0].text");
    }

    private boolean containsEducationScopeSignal(NewsArticle newsArticle) {
        String text = normalize(String.join(" ",
                safe(newsArticle.getTitle()),
                safe(newsArticle.getUrl()),
                safe(newsArticle.getSummary()),
                safe(newsArticle.getContent())
        ));

        return containsAny(text,
                "educacion", "educativo", "educativa", "docente", "docentes", "profesor", "profesora", "profesorado",
                "maestro", "maestra", "maestros", "maestras", "colegio", "colegios", "instituto", "institutos",
                "centro educativo", "centros educativos", "aula", "aulas", "alumnado", "universidad", "universitario",
                "fp", "formacion profesional", "oposicion", "oposiciones", "interino", "interinos", "sipri",
                "bolsa docente", "bolsas docentes", "plantilla docente", "plantillas docentes", "retribucion", "retribuciones",
                "curriculo", "curricular", "inspeccion educativa", "inclusion educativa", "digitalizacion educativa",
                "consejeria de desarrollo educativo", "consejeria de educacion", "junta de andalucia", "boja",
                "sindicato docente", "sindicatos docentes", "mesa sectorial", "ensenanza", "escuela", "escuelas"
        );
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    private String enrichContentIfNeeded(NewsArticle newsArticle) {
        String content = newsArticle.getContent();
        if (!hasInsufficientLocalContext(newsArticle)) {
            return content;
        }

        try {
            return newsContentEnrichmentPort.enrich(newsArticle.getUrl())
                    .filter(enriched -> !enriched.isBlank())
                    .map(enriched -> mergeContent(content, enriched))
                    .orElse(content);
        } catch (RuntimeException exception) {
            log.warn("classification url enrichment skipped: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            return content;
        }
    }

    private boolean hasInsufficientLocalContext(NewsArticle newsArticle) {
        String text = String.join(" ",
                safe(newsArticle.getTitle()),
                safe(newsArticle.getSummary()),
                safe(newsArticle.getContent())
        ).replaceAll("\\s+", " ").trim();
        return text.length() < 350;
    }

    private String mergeContent(String originalContent, String enrichedContent) {
        String original = safe(originalContent).trim();
        String enriched = safe(enrichedContent).trim();
        if (original.isBlank()) {
            return "Contexto enriquecido desde la URL:\n" + enriched;
        }

        return original + "\n\nContexto enriquecido desde la URL:\n" + enriched;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    private Map<String, Object> classificationDetails(NewsArticle newsArticle, NewsClassification classification, String aiSummary) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF02_CLASSIFICATION");
        details.put("newsId", newsArticle.getId());
        details.put("newsTitle", abbreviate(newsArticle.getTitle()));
        details.put("category", classification.getCategory().name());
        details.put("subcategory", classification.getSubcategory());
        details.put("relevance", classification.getRelevanceScore());
        details.put("impact", classification.getImpactLevel().name());
        details.put("urgency", classification.getUrgencyLevel().name());
        details.put("finalNewsStatus", newsArticle.getProcessingStatus().name());
        details.put("discarded", newsArticle.getProcessingStatus().name().equals("DISCARDED"));
        if (newsArticle.getProcessingStatus().name().equals("DISCARDED")) {
            details.put("discardReason", classification.getSubcategory());
        } else {
            details.put("keywords", classification.getKeywords());
            details.put("entities", classification.getEntities());
            details.put("aiSummary", abbreviate(aiSummary));
        }
        return details;
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
