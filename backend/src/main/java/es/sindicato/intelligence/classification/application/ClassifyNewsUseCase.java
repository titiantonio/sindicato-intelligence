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
    private final ClassifiedNewsFollowUpPort classifiedNewsFollowUpPort;

    public ClassifyNewsUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            ClassifyNewsPromptBuilder promptBuilder,
            AIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder,
            AiModelExecutionCoordinator aiModelExecutionCoordinator,
            NewsContentEnrichmentPort newsContentEnrichmentPort,
            ClassifiedNewsFollowUpPort classifiedNewsFollowUpPort
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
        this.newsContentEnrichmentPort = newsContentEnrichmentPort;
        this.classifiedNewsFollowUpPort = classifiedNewsFollowUpPort;
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

        ClassificationContext classificationContext = classificationContext(newsArticle);
        ClassifyNewsPrompt prompt = promptBuilder.build(
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                classificationContext.effectiveContent()
        );
        ClassificationAttempt classificationAttempt;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            classificationAttempt = aiModelExecutionCoordinator.execute("WF02_CLASSIFICATION", () -> classifyWithProvider(newsArticle, classificationContext.effectiveContent(), prompt));
        } catch (RuntimeException exception) {
            ClassificationAIResponse fallbackResponse = fallbackForOutOfScopeResponseWithoutText(newsArticle, exception);
            if (fallbackResponse != null) {
                classificationAttempt = new ClassificationAttempt(fallbackResponse, false, true, "PROVIDER_NO_TEXT_OUT_OF_SCOPE");
                log.warn("classification used out-of-scope fallback after provider response without text: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            } else {
                metricsRecorder.recordFailure(
                        "CLASSIFICATION",
                        "WF02_CLASSIFICATION",
                        aiProvider.providerName(),
                        aiProvider.modelName(),
                        "NEWS",
                        newsArticle.getId(),
                        startedAt,
                        exception,
                        classificationFailureDetails(newsArticle, classificationContext, exception)
                );
                log.error("classification failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
                throw exception;
            }
        }
        ClassificationAIResponse aiResponse;
        try {
            aiResponse = normalizeAndValidate(classificationAttempt.response());
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure(
                    "CLASSIFICATION",
                    "WF02_CLASSIFICATION",
                    aiProvider.providerName(),
                    aiProvider.modelName(),
                    "NEWS",
                    newsArticle.getId(),
                    startedAt,
                    exception,
                    classificationFailureDetails(newsArticle, classificationContext, exception)
            );
            log.error("classification response validation failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
            throw exception;
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

        requestEventDetectionIfClassified(newsArticle, classification);

        metricsRecorder.recordSuccess(
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                aiProvider.providerName(),
                aiProvider.modelName(),
                "NEWS",
                newsArticle.getId(),
                startedAt,
                classificationDetails(newsArticle, savedClassification, aiResponse, classificationContext, classificationAttempt)
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

    private void requestEventDetectionIfClassified(NewsArticle newsArticle, NewsClassification classification) {
        if (classification.isDiscardableForEventDetection()) {
            return;
        }

        try {
            classifiedNewsFollowUpPort.requestEventDetection(newsArticle.getId());
        } catch (RuntimeException exception) {
            log.warn("classification follow-up event detection request failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
        }
    }

    private ClassificationAttempt classifyWithProvider(NewsArticle newsArticle, String effectiveContent, ClassifyNewsPrompt prompt) {
        try {
            ClassificationAIResponse response = aiProvider.classify(new ClassificationAIRequest(
                    newsArticle.getTitle(),
                    newsArticle.getUrl(),
                    newsArticle.getSummary(),
                    effectiveContent,
                    prompt.systemPrompt(),
                    prompt.userPrompt()
            ));
            return new ClassificationAttempt(response, false, false, null);
        } catch (RuntimeException exception) {
            if (!isProviderResponseWithoutText(exception) || !containsEducationScopeSignal(newsArticle)) {
                throw exception;
            }

            log.warn("classification retrying with reduced context after provider response without text: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            String reducedContent = reducedContentForNoTextRetry();
            ClassifyNewsPrompt reducedPrompt = promptBuilder.build(
                    newsArticle.getTitle(),
                    newsArticle.getUrl(),
                    newsArticle.getSummary(),
                    reducedContent
            );

            ClassificationAIResponse response = aiProvider.classify(new ClassificationAIRequest(
                    newsArticle.getTitle(),
                    newsArticle.getUrl(),
                    newsArticle.getSummary(),
                    reducedContent,
                    reducedPrompt.systemPrompt(),
                    reducedPrompt.userPrompt()
            ));
            return new ClassificationAttempt(response, true, false, "PROVIDER_NO_TEXT_REDUCED_CONTEXT");
        }
    }

    private ClassificationAIResponse normalizeAndValidate(ClassificationAIResponse response) {
        Objects.requireNonNull(response, "classification response is required");
        Objects.requireNonNull(response.category(), "classification category is required");
        Objects.requireNonNull(response.relevance(), "classification relevance is required");
        Objects.requireNonNull(response.impact(), "classification impact is required");
        Objects.requireNonNull(response.urgency(), "classification urgency is required");

        if (isDiscardResponse(response)) {
            return new ClassificationAIResponse(
                    ClassificationCategory.OTROS,
                    response.subcategory(),
                    BigDecimal.ZERO,
                    ImpactLevel.LOW,
                    UrgencyLevel.LOW,
                    List.of(),
                    List.of(),
                    null,
                    response.classificationReason()
            );
        }

        if (response.relevance().compareTo(BigDecimal.ZERO) == 0 && response.category() != ClassificationCategory.OTROS) {
            throw new IllegalArgumentException("classification response incoherent: non-discarded category cannot have zero relevance");
        }
        if (response.relevance().compareTo(BigDecimal.valueOf(70)) >= 0 && response.impact() == ImpactLevel.LOW) {
            throw new IllegalArgumentException("classification response incoherent: high relevance cannot have LOW impact");
        }
        if (response.relevance().compareTo(BigDecimal.valueOf(90)) >= 0
                && response.impact() != ImpactLevel.HIGH
                && response.impact() != ImpactLevel.CRITICAL) {
            throw new IllegalArgumentException("classification response incoherent: critical relevance requires HIGH or CRITICAL impact");
        }

        return response;
    }

    private boolean isDiscardResponse(ClassificationAIResponse response) {
        if (response.category() != ClassificationCategory.OTROS || response.subcategory() == null) {
            return false;
        }
        String normalized = response.subcategory().trim();
        return "FUERA_DE_AMBITO".equalsIgnoreCase(normalized)
                || "INFORMACION_INSUFICIENTE".equalsIgnoreCase(normalized);
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
                null,
                "Respuesta sin texto del proveedor para noticia sin senales educativas capturadas."
        );
    }

    private boolean isProviderResponseWithoutText(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && message.startsWith("Gemini response does not contain candidates[0].content.parts[0].text");
    }

    private String reducedContentForNoTextRetry() {
        return "Contexto reducido tras respuesta IA sin texto. Clasifica solo con titulo, URL y resumen capturado por WF-01.";
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

    private ClassificationContext classificationContext(NewsArticle newsArticle) {
        String content = newsArticle.getContent();
        if (!hasInsufficientLocalContext(newsArticle)) {
            return new ClassificationContext(content, false);
        }

        try {
            return newsContentEnrichmentPort.enrich(newsArticle.getUrl())
                    .filter(enriched -> !enriched.isBlank())
                    .map(enriched -> new ClassificationContext(mergeContent(content, enriched), true))
                    .orElse(new ClassificationContext(content, false));
        } catch (RuntimeException exception) {
            log.warn("classification url enrichment skipped: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            return new ClassificationContext(content, false);
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

    private Map<String, Object> classificationDetails(
            NewsArticle newsArticle,
            NewsClassification classification,
            ClassificationAIResponse aiResponse,
            ClassificationContext classificationContext,
            ClassificationAttempt classificationAttempt
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF02_CLASSIFICATION");
        details.put("newsId", newsArticle.getId());
        details.put("newsTitle", abbreviate(newsArticle.getTitle()));
        details.put("prioritySignals", prioritySignals(newsArticle));
        details.put("urlEnriched", classificationContext.urlEnriched());
        details.put("reducedContextRetry", classificationAttempt.reducedContextRetry());
        details.put("fallbackUsed", classificationAttempt.fallbackUsed());
        if (classificationAttempt.recoveryReason() != null) {
            details.put("recoveryReason", classificationAttempt.recoveryReason());
        }
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
            details.put("aiSummary", abbreviate(aiResponse.summary()));
            details.put("classificationReason", abbreviate(aiResponse.classificationReason()));
        }
        return details;
    }

    private Map<String, Object> classificationFailureDetails(
            NewsArticle newsArticle,
            ClassificationContext classificationContext,
            RuntimeException exception
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF02_CLASSIFICATION");
        details.put("newsId", newsArticle.getId());
        details.put("newsTitle", abbreviate(newsArticle.getTitle()));
        details.put("prioritySignals", prioritySignals(newsArticle));
        details.put("urlEnriched", classificationContext.urlEnriched());
        details.put("providerResponseWithoutText", isProviderResponseWithoutText(exception));
        details.put("educationScopeSignals", containsEducationScopeSignal(newsArticle));
        details.put("finalNewsStatus", newsArticle.getProcessingStatus().name());
        return details;
    }

    public List<String> prioritySignals(NewsArticle newsArticle) {
        String text = normalize(String.join(" ",
                safe(newsArticle.getTitle()),
                safe(newsArticle.getUrl()),
                safe(newsArticle.getSummary()),
                safe(newsArticle.getContent())
        ));

        return java.util.stream.Stream.of(
                        signal(text, "boja", "BOJA"),
                        signal(text, "sipri", "SIPRI"),
                        signal(text, "plazo", "PLAZO"),
                        signal(text, "convocatoria", "CONVOCATORIA"),
                        signal(text, "oposicion", "OPOSICIONES"),
                        signal(text, "adjudicacion", "ADJUDICACION"),
                        signal(text, "mesa sectorial", "MESA_SECTORIAL"),
                        signal(text, "huelga", "HUELGA")
                )
                .filter(Objects::nonNull)
                .toList();
    }

    private String signal(String text, String needle, String signal) {
        return text.contains(needle) ? signal : null;
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

    private record ClassificationContext(String effectiveContent, boolean urlEnriched) {
    }

    private record ClassificationAttempt(
            ClassificationAIResponse response,
            boolean reducedContextRetry,
            boolean fallbackUsed,
            String recoveryReason
    ) {
    }
}
