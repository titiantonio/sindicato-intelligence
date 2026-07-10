package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
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
            RelevantContentLinkExtractor relevantContentLinkExtractor
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
    }

    @Transactional
    public GeneratedContent execute(GenerateContentCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.eventId(), "eventId is required");

        String channel = normalize(command.channel(), DEFAULT_CHANNEL);
        String tone = normalize(command.tone(), DEFAULT_TONE);
        String length = normalize(command.length(), DEFAULT_LENGTH);
        Long createdBy = authorProvider.currentAuthorId();

        log.info("content generation started: eventId={}, analysisId={}, channel={}, tone={}, length={}, createdBy={}", command.eventId(), command.analysisId(), channel, tone, length, createdBy);

        Event event = eventRepository.findById(command.eventId())
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + command.eventId()));
        EventAIAnalysis analysis = resolveAnalysis(command, event.getId());
        List<NewsArticle> newsArticles = loadNewsArticles(event);
        List<RelevantContentLink> relevantLinks = relevantContentLinkExtractor.extract(newsArticles);
        GenerateContentPrompt prompt = promptBuilder.build(new ContentAIRequest(
                event,
                analysis,
                channel,
                tone,
                length,
                relevantLinks,
                "",
                ""
        ));
        log.info("content generation context loaded: eventId={}, analysisId={}, relevantLinks={}", event.getId(), analysis.getId(), relevantLinks.size());

        ContentAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            aiResponse = aiProvider.generate(new ContentAIRequest(event, analysis, channel, tone, length, relevantLinks, prompt.systemPrompt(), prompt.userPrompt()));
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
                aiResponse.title(),
                buildContent(aiResponse),
                ContentStatus.PENDING_REVIEW,
                OffsetDateTime.now(),
                null
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

        log.info("content generation completed: eventId={}, analysisId={}, contentId={}, status={}, channel={}, tone={}", event.getId(), analysis.getId(), savedContent.getId(), savedContent.getStatus(), savedContent.getChannel(), savedContent.getTone());

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
        details.put("length", length);
        details.put("title", abbreviate(content.getTitle()));
        details.put("excerpt", abbreviate(content.getContent()));
        details.put("hashtags", hashtags == null ? List.of() : hashtags);
        details.put("relevantLinks", relevantLinks == null ? List.of() : relevantLinks.stream().map(RelevantContentLink::url).limit(5).toList());
        details.put("editorialStatus", content.getStatus().name());
        details.put("createdBy", content.getCreatedBy());
        return details;
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

    private EventAIAnalysis resolveAnalysis(GenerateContentCommand command, Long eventId) {
        if (command.analysisId() != null) {
            EventAIAnalysis analysis = analysisRepository.findById(command.analysisId())
                    .orElseThrow(() -> new IllegalArgumentException("analysis not found: " + command.analysisId()));
            if (!analysis.getEventId().equals(eventId)) {
                log.warn("content generation skipped because analysis belongs to another event: eventId={}, analysisId={}, analysisEventId={}", eventId, analysis.getId(), analysis.getEventId());
                throw new IllegalArgumentException("analysis does not belong to event");
            }
            return analysis;
        }

        List<EventAIAnalysis> analyses = analysisRepository.findByEventId(eventId);
        if (analyses.isEmpty()) {
            log.warn("content generation skipped because event has no analysis: eventId={}", eventId);
            throw new IllegalArgumentException("event analysis not found: " + eventId);
        }

        return analyses.getFirst();
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
}
