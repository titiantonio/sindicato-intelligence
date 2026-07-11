package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GenerateAnalysisUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateAnalysisUseCase.class);

    private final EventRepository eventRepository;
    private final NewsRepository newsRepository;
    private final EventAIAnalysisRepository analysisRepository;
    private final GenerateAnalysisPromptBuilder promptBuilder;
    private final AnalysisAIProvider aiProvider;
    private final AiOperationMetricsRecorder metricsRecorder;
    private final RecordAuditLogUseCase recordAuditLogUseCase;
    private final AiModelExecutionCoordinator aiModelExecutionCoordinator;

    public GenerateAnalysisUseCase(
            EventRepository eventRepository,
            NewsRepository newsRepository,
            EventAIAnalysisRepository analysisRepository,
            GenerateAnalysisPromptBuilder promptBuilder,
            AnalysisAIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder,
            RecordAuditLogUseCase recordAuditLogUseCase,
            AiModelExecutionCoordinator aiModelExecutionCoordinator
    ) {
        this.eventRepository = eventRepository;
        this.newsRepository = newsRepository;
        this.analysisRepository = analysisRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
    }

    @Transactional
    public EventAIAnalysis execute(GenerateAnalysisCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.eventId(), "eventId is required");

        log.info("analysis generation started: eventId={}", command.eventId());

        Event event = eventRepository.findById(command.eventId())
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + command.eventId()));
        List<NewsArticle> newsArticles = loadNewsArticles(event);
        List<AnalysisNewsItem> newsItems = newsArticles.stream().map(this::toNewsItem).toList();
        GenerateAnalysisPrompt prompt = promptBuilder.build(new AnalysisAIRequest(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getImportance(),
                newsItems,
                "",
                ""
        ));
        log.info("analysis generation context loaded: eventId={}, newsCount={}", event.getId(), newsArticles.size());

        AnalysisAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            aiResponse = aiModelExecutionCoordinator.execute("WF04_ANALYSIS", () -> aiProvider.generate(new AnalysisAIRequest(
                            event.getId(),
                            event.getTitle(),
                            event.getDescription(),
                            event.getCategory(),
                            event.getImportance(),
                            newsItems,
                            prompt.systemPrompt(),
                            prompt.userPrompt()
                    )));
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure("ANALYSIS", "WF04_ANALYSIS", aiProvider.providerName(), aiProvider.modelName(), "EVENT", event.getId(), startedAt, exception);
            log.error("analysis generation failed during AI generation: eventId={}, reason={}", event.getId(), exception.getMessage(), exception);
            throw exception;
        }
        EventAIAnalysis analysis = new EventAIAnalysis(
                null,
                event.getId(),
                aiResponse.executiveSummary(),
                aiResponse.unionSummary(),
                aiResponse.keyPoints(),
                aiResponse.risks(),
                aiResponse.opportunities(),
                aiResponse.modelUsed(),
                OffsetDateTime.now()
        );
        EventAIAnalysis savedAnalysis = analysisRepository.save(analysis);
        recordAuditLogUseCase.record(
                "ANALYSIS_GENERATED",
                "ANALYSIS",
                savedAnalysis.getId(),
                null,
                AuditDetailFormatter.analysisGenerated(
                        savedAnalysis.getId(),
                        savedAnalysis.getEventId(),
                        savedAnalysis.getKeyPoints().size(),
                        savedAnalysis.getRisks().size(),
                        savedAnalysis.getOpportunities().size(),
                        savedAnalysis.getModelUsed()
                )
        );

        metricsRecorder.recordSuccess(
                "ANALYSIS",
                "WF04_ANALYSIS",
                aiProvider.providerName(),
                aiResponse.modelUsed(),
                "EVENT",
                event.getId(),
                startedAt,
                analysisDetails(event, newsArticles.size(), savedAnalysis)
        );

        log.info(
                "analysis generation completed: eventId={}, analysisId={}, keyPoints={}, risks={}, opportunities={}, modelUsed={}",
                event.getId(),
                savedAnalysis.getId(),
                savedAnalysis.getKeyPoints().size(),
                savedAnalysis.getRisks().size(),
                savedAnalysis.getOpportunities().size(),
                savedAnalysis.getModelUsed()
        );

        return savedAnalysis;
    }

    private Map<String, Object> analysisDetails(Event event, int newsCount, EventAIAnalysis analysis) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF04_ANALYSIS");
        details.put("eventId", event.getId());
        details.put("eventTitle", abbreviate(event.getTitle()));
        details.put("eventCategory", event.getCategory().name());
        details.put("eventImportance", event.getImportance().name());
        details.put("newsCount", newsCount);
        details.put("analysisId", analysis.getId());
        details.put("executiveSummary", abbreviate(analysis.getExecutiveSummary()));
        details.put("unionSummary", abbreviate(analysis.getUnionSummary()));
        details.put("keyPoints", abbreviateList(analysis.getKeyPoints()));
        details.put("risks", abbreviateList(analysis.getRisks()));
        details.put("opportunities", abbreviateList(analysis.getOpportunities()));
        details.put("modelUsed", analysis.getModelUsed());
        return details;
    }

    private List<String> abbreviateList(List<String> values) {
        return values.stream().limit(5).map(this::abbreviate).toList();
    }

    private String abbreviate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmed = value.replaceAll("\\s+", " ").trim();
        if (trimmed.length() <= 220) {
            return trimmed;
        }

        return trimmed.substring(0, 217) + "...";
    }

    private List<NewsArticle> loadNewsArticles(Event event) {
        return event.getNewsIds().stream()
                .map(newsId -> newsRepository.findById(newsId)
                        .orElseThrow(() -> {
                            log.warn("analysis generation skipped because event news does not exist: eventId={}, newsId={}", event.getId(), newsId);
                            return new IllegalArgumentException("event news not found: " + newsId);
                        }))
                .toList();
    }

    private AnalysisNewsItem toNewsItem(NewsArticle newsArticle) {
        return new AnalysisNewsItem(
                newsArticle.getId(),
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                newsArticle.getPublishedAt()
        );
    }
}
