package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.ContentType;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventMatchDecision;
import es.sindicato.intelligence.event.domain.EventNewsAssociationTrace;
import es.sindicato.intelligence.event.domain.EventNewsAssociationTraceRepository;
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
public class GenerateContentUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateContentUseCase.class);
    private static final String DEFAULT_CHANNEL = "TELEGRAM";
    private static final String DEFAULT_TONE = "INFORMATIVO";
    private static final String DEFAULT_LENGTH = "STANDARD";

    private final EventRepository eventRepository;
    private final NewsRepository newsRepository;
    private final EventAIAnalysisRepository analysisRepository;
    private final GeneratedContentRepository contentRepository;
    private final GenerateContentPromptBuilder promptBuilder;
    private final ContentAIProvider aiProvider;
    private final CurrentContentAuthorProvider authorProvider;
    private final AiOperationMetricsRecorder metricsRecorder;
    private final RecordAuditLogUseCase recordAuditLogUseCase;
    private final RelevantContentLinkExtractor relevantContentLinkExtractor;
    private final AiModelExecutionCoordinator aiModelExecutionCoordinator;
    private final EventNewsAssociationTraceRepository eventNewsAssociationTraceRepository;
    private final ContentAIResponseValidator contentAIResponseValidator;

    public GenerateContentUseCase(
            EventRepository eventRepository,
            NewsRepository newsRepository,
            EventAIAnalysisRepository analysisRepository,
            GeneratedContentRepository contentRepository,
            GenerateContentPromptBuilder promptBuilder,
            ContentAIProvider aiProvider,
            CurrentContentAuthorProvider authorProvider,
            AiOperationMetricsRecorder metricsRecorder,
            RecordAuditLogUseCase recordAuditLogUseCase,
            RelevantContentLinkExtractor relevantContentLinkExtractor,
            AiModelExecutionCoordinator aiModelExecutionCoordinator,
            EventNewsAssociationTraceRepository eventNewsAssociationTraceRepository,
            ContentAIResponseValidator contentAIResponseValidator
    ) {
        this.eventRepository = eventRepository;
        this.newsRepository = newsRepository;
        this.analysisRepository = analysisRepository;
        this.contentRepository = contentRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.authorProvider = authorProvider;
        this.metricsRecorder = metricsRecorder;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
        this.relevantContentLinkExtractor = relevantContentLinkExtractor;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
        this.eventNewsAssociationTraceRepository = eventNewsAssociationTraceRepository;
        this.contentAIResponseValidator = contentAIResponseValidator;
    }

    @Transactional
    public GeneratedContent execute(GenerateContentCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.eventId(), "eventId is required");

        String channel = normalize(command.channel(), DEFAULT_CHANNEL);
        String tone = normalize(command.tone(), DEFAULT_TONE);
        String length = normalizeLength(command.length());
        ContentType contentType = resolveContentType(command.contentType(), length);
        Long createdBy = authorProvider.currentAuthorId();

        log.info("content generation started: eventId={}, analysisId={}, channel={}, tone={}, contentType={}, length={}, createdBy={}", command.eventId(), command.analysisId(), channel, tone, contentType, length, createdBy);

        Event event = eventRepository.findById(command.eventId())
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + command.eventId()));
        validateEventOperable(event);
        EventAIAnalysis analysis = resolveAnalysis(command, event);
        validateAnalysisCurrent(event, analysis);
        validateNoActiveDuplicate(event, analysis, channel, contentType);
        List<NewsArticle> newsArticles = loadNewsArticles(event);
        List<RelevantContentLink> relevantLinks = relevantContentLinkExtractor.extract(newsArticles);
        List<EventNewsAssociationTrace> associationTraces = eventNewsAssociationTraceRepository.findByEventId(event.getId());
        ContentGenerationContext generationContext = generationContext(event, associationTraces);
        GenerateContentPrompt prompt = promptBuilder.build(new ContentAIRequest(
                event,
                analysis,
                channel,
                tone,
                contentType,
                length,
                relevantLinks,
                generationContext,
                "",
                ""
        ));
        log.info("content generation context loaded: eventId={}, analysisId={}, contentType={}, relevantLinks={}, associationTraces={}, averageConfidence={}", event.getId(), analysis.getId(), contentType, relevantLinks.size(), associationTraces.size(), generationContext.averageConfidence());

        ContentAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            ContentAIRequest aiRequest = new ContentAIRequest(event, analysis, channel, tone, contentType, length, relevantLinks, generationContext, prompt.systemPrompt(), prompt.userPrompt());
            aiResponse = aiModelExecutionCoordinator.execute("WF05_CONTENT", () -> aiProvider.generate(aiRequest));
            contentAIResponseValidator.validate(aiResponse, relevantLinks, contentType, length);
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure("CONTENT_GENERATION", "WF05_CONTENT", aiProvider.providerName(), aiProvider.modelName(), "EVENT", event.getId(), startedAt, exception);
            log.error("content generation failed during AI generation: eventId={}, analysisId={}, reason={}", event.getId(), analysis.getId(), exception.getMessage(), exception);
            throw exception;
        }
        GeneratedContent content = new GeneratedContent(
                null,
                event.getId(),
                analysis.getId(),
                createdBy,
                channel,
                tone,
                contentType,
                length,
                aiResponse.title(),
                buildContent(aiResponse),
                ContentStatus.PENDING_REVIEW,
                OffsetDateTime.now(),
                null,
                contentMetadata(event, analysis, length, contentType, relevantLinks, generationContext)
        );
        GeneratedContent savedContent = contentRepository.save(content);
        recordAuditLogUseCase.record(
                "CONTENT_GENERATED",
                "CONTENT",
                savedContent.getId(),
                null,
                AuditDetailFormatter.contentGenerated(
                        savedContent.getId(),
                        savedContent.getEventId(),
                        savedContent.getAnalysisId(),
                        savedContent.getChannel(),
                        savedContent.getTone(),
                        savedContent.getStatus().name()
                )
        );

        metricsRecorder.recordSuccess(
                "CONTENT_GENERATION",
                "WF05_CONTENT",
                aiProvider.providerName(),
                aiProvider.modelName(),
                "EVENT",
                event.getId(),
                startedAt,
                contentDetails(event, analysis, savedContent, length, aiResponse.hashtags(), relevantLinks)
        );

        log.info("content generation completed: eventId={}, analysisId={}, contentId={}, status={}, channel={}, tone={}, contentType={}", event.getId(), analysis.getId(), savedContent.getId(), savedContent.getStatus(), savedContent.getChannel(), savedContent.getTone(), savedContent.getContentType());

        return savedContent;
    }

    private Map<String, Object> contentDetails(
            Event event,
            EventAIAnalysis analysis,
            GeneratedContent content,
            String length,
            List<String> hashtags,
            List<RelevantContentLink> relevantLinks
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF05_CONTENT");
        details.put("eventId", event.getId());
        details.put("eventTitle", abbreviate(event.getTitle()));
        details.put("analysisId", analysis.getId());
        details.put("contentId", content.getId());
        details.put("channel", content.getChannel());
        details.put("tone", content.getTone());
        details.put("contentType", content.getContentType().name());
        details.put("length", length);
        details.put("title", abbreviate(content.getTitle()));
        details.put("excerpt", abbreviate(content.getContent()));
        details.put("hashtags", hashtags == null ? List.of() : hashtags);
        details.put("relevantLinks", relevantLinks == null ? List.of() : relevantLinks.stream().map(RelevantContentLink::url).limit(5).toList());
        details.put("analysisType", analysis.getAnalysisType().name());
        details.put("generationTrigger", analysis.getGenerationTrigger().name());
        details.put("affectedGroups", abbreviateList(analysis.getAffectedGroups()));
        details.put("recommendedMonitoring", abbreviateList(analysis.getRecommendedMonitoring()));
        details.put("editorialStatus", content.getStatus().name());
        details.put("createdBy", content.getCreatedBy());
        return details;
    }

    private Map<String, Object> contentMetadata(
            Event event,
            EventAIAnalysis analysis,
            String length,
            ContentType contentType,
            List<RelevantContentLink> relevantLinks,
            ContentGenerationContext generationContext
    ) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("workflowCode", "WF05_CONTENT");
        metadata.put("analysisCurrent", !analysis.isOutdatedFor(event.getUpdatedAt()));
        metadata.put("eventUpdatedAt", event.getUpdatedAt().toString());
        metadata.put("analysisEventUpdatedAtSnapshot", analysis.getEventUpdatedAtSnapshot().toString());
        metadata.put("analysisType", analysis.getAnalysisType().name());
        metadata.put("generationTrigger", analysis.getGenerationTrigger().name());
        metadata.put("contentType", contentType.name());
        metadata.put("length", length);
        metadata.put("relevantLinks", relevantLinks.stream().map(RelevantContentLink::url).limit(5).toList());
        metadata.put("newsCount", generationContext.newsCount());
        metadata.put("tracedAssociations", generationContext.tracedAssociations());
        metadata.put("averageConfidence", generationContext.averageConfidence() == null ? "Sin dato" : generationContext.averageConfidence());
        metadata.put("hasReviewRecommendedMatches", generationContext.hasReviewRecommendedMatches());
        metadata.put("matchDecisions", generationContext.matchDecisions());
        return metadata;
    }

    private List<String> abbreviateList(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(this::abbreviate).limit(5).toList();
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

    private EventAIAnalysis resolveAnalysis(GenerateContentCommand command, Event event) {
        if (command.analysisId() != null) {
            EventAIAnalysis analysis = analysisRepository.findById(command.analysisId())
                    .orElseThrow(() -> new IllegalArgumentException("analysis not found: " + command.analysisId()));
            if (!analysis.getEventId().equals(event.getId())) {
                log.warn("content generation skipped because analysis belongs to another event: eventId={}, analysisId={}, analysisEventId={}", event.getId(), analysis.getId(), analysis.getEventId());
                throw new IllegalArgumentException("analysis does not belong to event");
            }
            return analysis;
        }

        List<EventAIAnalysis> analyses = analysisRepository.findByEventId(event.getId()).stream()
                .sorted((left, right) -> {
                    int generatedComparison = right.getGeneratedAt().compareTo(left.getGeneratedAt());
                    if (generatedComparison != 0) {
                        return generatedComparison;
                    }
                    Long rightId = right.getId() == null ? 0L : right.getId();
                    Long leftId = left.getId() == null ? 0L : left.getId();
                    return rightId.compareTo(leftId);
                })
                .toList();
        if (analyses.isEmpty()) {
            log.warn("content generation skipped because event has no analysis: eventId={}", event.getId());
            throw new IllegalArgumentException("event analysis not found: " + event.getId());
        }

        return analyses.stream()
                .filter(analysis -> !analysis.isOutdatedFor(event.getUpdatedAt()))
                .findFirst()
                .orElse(analyses.getFirst());
    }

    private void validateEventOperable(Event event) {
        if (!event.isActive() || event.isManualDiscarded()) {
            log.warn("content generation skipped because event is not operable: eventId={}, status={}, manualDiscarded={}", event.getId(), event.getStatus(), event.isManualDiscarded());
            throw new IllegalStateException("content can only be generated for active non-discarded events");
        }
    }

    private void validateAnalysisCurrent(Event event, EventAIAnalysis analysis) {
        if (analysis.isOutdatedFor(event.getUpdatedAt())) {
            log.warn("content generation skipped because analysis is outdated: eventId={}, analysisId={}, eventUpdatedAt={}, analysisSnapshot={}", event.getId(), analysis.getId(), event.getUpdatedAt(), analysis.getEventUpdatedAtSnapshot());
            throw new IllegalStateException("analysis is outdated; regenerate analysis before generating content");
        }
    }

    private void validateNoActiveDuplicate(Event event, EventAIAnalysis analysis, String channel, ContentType contentType) {
        if (contentRepository.existsActiveByEventIdAndAnalysisIdAndChannelAndContentType(event.getId(), analysis.getId(), channel, contentType)) {
            log.warn("content generation skipped because active content already exists: eventId={}, analysisId={}, channel={}, contentType={}", event.getId(), analysis.getId(), channel, contentType);
            throw new IllegalStateException("active generated content already exists for this event, analysis, channel and content type");
        }
    }

    private ContentGenerationContext generationContext(Event event, List<EventNewsAssociationTrace> traces) {
        List<EventNewsAssociationTrace> safeTraces = traces == null ? List.of() : traces;
        List<Integer> confidenceScores = safeTraces.stream()
                .map(EventNewsAssociationTrace::confidenceScore)
                .filter(Objects::nonNull)
                .toList();
        Integer averageConfidence = confidenceScores.isEmpty()
                ? null
                : Math.round((float) confidenceScores.stream().mapToInt(Integer::intValue).average().orElse(0));
        List<String> decisions = safeTraces.stream()
                .map(EventNewsAssociationTrace::matchDecision)
                .filter(Objects::nonNull)
                .map(EventMatchDecision::name)
                .distinct()
                .toList();
        boolean hasReviewRecommended = decisions.stream().anyMatch(decision -> decision.startsWith("REVIEW_RECOMMENDED"));
        List<String> reasons = safeTraces.stream()
                .map(EventNewsAssociationTrace::matchReason)
                .filter(reason -> reason != null && !reason.isBlank())
                .map(this::abbreviate)
                .limit(5)
                .toList();

        return new ContentGenerationContext(event.getNewsIds().size(), safeTraces.size(), averageConfidence, hasReviewRecommended, decisions, reasons);
    }

    private List<NewsArticle> loadNewsArticles(Event event) {
        return event.getNewsIds().stream()
                .map(newsId -> newsRepository.findById(newsId)
                        .orElseThrow(() -> {
                            log.warn("content generation skipped because event news does not exist: eventId={}, newsId={}", event.getId(), newsId);
                            return new IllegalArgumentException("event news not found: " + newsId);
                        }))
                .toList();
    }

    private String buildContent(ContentAIResponse response) {
        if (response.hashtags() == null || response.hashtags().isEmpty()) {
            return response.message();
        }

        return response.message() + "\n\n" + String.join(" ", response.hashtags());
    }

    private String normalize(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value.trim().toUpperCase();
    }

    private String normalizeLength(String value) {
        String normalized = normalize(value, DEFAULT_LENGTH);
        if ("MEDIUM".equals(normalized)) {
            return "STANDARD";
        }
        if ("LONG".equals(normalized) || "SHORT".equals(normalized) || "STANDARD".equals(normalized)) {
            return normalized;
        }
        return DEFAULT_LENGTH;
    }

    private ContentType resolveContentType(String value, String length) {
        if (value != null && !value.isBlank()) {
            return ContentType.valueOf(value.trim().toUpperCase());
        }
        if ("SHORT".equals(length)) {
            return ContentType.TELEGRAM_SHORT;
        }
        if ("LONG".equals(length)) {
            return ContentType.UNION_STATEMENT;
        }
        return ContentType.TELEGRAM_POST;
    }
}
