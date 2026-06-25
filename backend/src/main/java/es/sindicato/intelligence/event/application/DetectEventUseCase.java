package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class DetectEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(DetectEventUseCase.class);
    private static final int AUTOMATIC_MATCH_THRESHOLD = 85;

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final EventRepository eventRepository;
    private final EventMatchPromptBuilder promptBuilder;
    private final EventMatchingAIProvider aiProvider;
    private final AiOperationMetricsRecorder metricsRecorder;

    public DetectEventUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventRepository eventRepository,
            EventMatchPromptBuilder promptBuilder,
            EventMatchingAIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.eventRepository = eventRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
    }

    @Transactional
    public DetectEventResult execute(DetectEventCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.newsId(), "newsId is required");

        log.info("event detection started: newsId={}", command.newsId());

        NewsArticle newsArticle = newsRepository.findById(command.newsId())
                .orElseThrow(() -> new IllegalArgumentException("news not found: " + command.newsId()));

        if (newsArticle.getProcessingStatus() != NewsStatus.CLASSIFIED) {
            log.warn("event detection skipped because news is not classified: newsId={}, status={}", newsArticle.getId(), newsArticle.getProcessingStatus());
            throw new IllegalArgumentException("news must be CLASSIFIED before event detection");
        }

        if (eventRepository.existsNewsAssociation(newsArticle.getId())) {
            log.warn("event detection skipped because news already belongs to an event: newsId={}", newsArticle.getId());
            throw new IllegalArgumentException("news already belongs to an event");
        }

        NewsClassification classification = classificationRepository.findByNewsId(newsArticle.getId())
                .orElseThrow(() -> new IllegalArgumentException("news classification not found: " + newsArticle.getId()));

        if (classification.isDiscardableForEventDetection()) {
            newsArticle.markDiscarded();
            newsRepository.save(newsArticle);
            log.warn(
                    "event detection skipped because news classification is outside event scope: newsId={}, category={}, subcategory='{}', relevance={}",
                    newsArticle.getId(),
                    classification.getCategory(),
                    classification.getSubcategory(),
                    classification.getRelevanceScore()
            );
            throw new IllegalArgumentException("discarded news cannot generate events");
        }

        EventCategory category = EventCategory.valueOf(classification.getCategory().name());
        List<Event> activeEvents = eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING)).stream()
                .filter(event -> event.getCategory() == category)
                .toList();
        List<EventMatchCandidate> candidates = activeEvents.stream()
                .map(event -> new EventMatchCandidate(event.getId(), event.getTitle(), event.getDescription(), event.getCategory()))
                .toList();
        log.info("event detection candidates loaded: newsId={}, category={}, candidateCount={}", newsArticle.getId(), category, candidates.size());
        EventMatchPrompt prompt = promptBuilder.build(newsArticle.getTitle(), newsArticle.getSummary(), newsArticle.getContent(), candidates);
        EventMatchingAIResponse aiResponse;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            aiResponse = aiProvider.match(new EventMatchingAIRequest(
                    newsArticle.getTitle(),
                    newsArticle.getSummary(),
                    newsArticle.getContent(),
                    candidates,
                    prompt.systemPrompt(),
                    prompt.userPrompt()
            ));
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure("EVENT_MATCHING", "WF03_EVENT_MATCHING", aiProvider.providerName(), aiProvider.modelName(), "NEWS", newsArticle.getId(), startedAt, exception);
            log.error("event detection failed during AI matching: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
            throw exception;
        }
        Event event = findAutomaticMatch(activeEvents, aiResponse);
        boolean created = false;
        boolean matched = event != null;

        if (event == null) {
            event = createEvent(newsArticle, category, importanceOf(classification.getImpactLevel()));
            created = true;
            log.info("event detection creating new event: newsId={}, category={}, importance={}", newsArticle.getId(), category, importanceOf(classification.getImpactLevel()));
        } else {
            event.addNews(newsArticle.getId(), OffsetDateTime.now());
            log.info("event detection matched existing event: newsId={}, eventId={}, confidence={}", newsArticle.getId(), event.getId(), aiResponse.confidence());
        }

        Event savedEvent = eventRepository.save(event);
        eventRepository.saveNewsAssociation(savedEvent.getId(), newsArticle.getId(), aiResponse.confidence());
        newsArticle.markEventMatched();
        newsRepository.save(newsArticle);

        DetectEventResult result = new DetectEventResult(
                savedEvent.getId(),
                newsArticle.getId(),
                created,
                matched,
                aiResponse.confidence(),
                aiResponse.reason(),
                savedEvent.getStatus()
        );
        metricsRecorder.recordSuccess(
                "EVENT_MATCHING",
                "WF03_EVENT_MATCHING",
                aiProvider.providerName(),
                aiProvider.modelName(),
                "NEWS",
                newsArticle.getId(),
                startedAt,
                eventDetectionDetails(newsArticle, classification, candidates.size(), aiResponse, result)
        );
        log.info(
                "event detection completed: newsId={}, eventId={}, created={}, matched={}, confidence={}, status={}",
                result.newsId(),
                result.eventId(),
                result.created(),
                result.matched(),
                result.confidence(),
                result.eventStatus()
        );

        return result;
    }

    private Map<String, Object> eventDetectionDetails(
            NewsArticle newsArticle,
            NewsClassification classification,
            int candidateCount,
            EventMatchingAIResponse aiResponse,
            DetectEventResult result
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF03_EVENT_MATCHING");
        details.put("newsId", newsArticle.getId());
        details.put("newsTitle", abbreviate(newsArticle.getTitle()));
        details.put("category", classification.getCategory().name());
        details.put("candidateCount", candidateCount);
        details.put("aiMatch", aiResponse.match());
        details.put("aiSuggestedEventId", aiResponse.eventId());
        details.put("confidence", aiResponse.confidence());
        details.put("automaticMatchThreshold", AUTOMATIC_MATCH_THRESHOLD);
        details.put("decision", result.created() ? "CREATED_EVENT" : "MATCHED_EXISTING_EVENT");
        details.put("created", result.created());
        details.put("matched", result.matched());
        details.put("finalEventId", result.eventId());
        details.put("eventStatus", result.eventStatus().name());
        details.put("reason", abbreviate(aiResponse.reason()));
        return details;
    }

    private String abbreviate(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String trimmed = value.replaceAll("\\s+", " ").trim();
        if (trimmed.length() <= 160) {
            return trimmed;
        }

        return trimmed.substring(0, 157) + "...";
    }

    private Event findAutomaticMatch(List<Event> activeEvents, EventMatchingAIResponse aiResponse) {
        if (!aiResponse.match() || aiResponse.confidence() < AUTOMATIC_MATCH_THRESHOLD || aiResponse.eventId() == null) {
            return null;
        }

        return activeEvents.stream()
                .filter(event -> event.getId().equals(aiResponse.eventId()))
                .findFirst()
                .orElse(null);
    }

    private Event createEvent(NewsArticle newsArticle, EventCategory category, Importance importance) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Event(
                null,
                newsArticle.getTitle(),
                descriptionOf(newsArticle),
                category,
                importance,
                EventStatus.OPEN,
                Set.of(newsArticle.getId()),
                now,
                now,
                now,
                now
        );
    }

    private String descriptionOf(NewsArticle newsArticle) {
        if (newsArticle.getSummary() != null && !newsArticle.getSummary().isBlank()) {
            return newsArticle.getSummary();
        }

        if (newsArticle.getContent() != null && !newsArticle.getContent().isBlank()) {
            return newsArticle.getContent();
        }

        return newsArticle.getTitle();
    }

    private Importance importanceOf(ImpactLevel impactLevel) {
        return Importance.valueOf(impactLevel.name());
    }
}
