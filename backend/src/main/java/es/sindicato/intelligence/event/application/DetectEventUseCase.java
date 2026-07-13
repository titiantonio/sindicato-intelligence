package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.automation.application.RequestImmediateAutomationWorkflowRunUseCase;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventMatchDecision;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DetectEventUseCase {

    private static final Logger log = LoggerFactory.getLogger(DetectEventUseCase.class);
    private static final int AUTOMATIC_MATCH_THRESHOLD = 85;
    private static final int REVIEW_RECOMMENDED_THRESHOLD = 70;
    private static final int MAX_CANDIDATES = 15;
    private static final int MAX_VERIFICATION_CANDIDATES = 5;
    private static final int MAX_RECENT_NEWS_TITLES = 3;
    private static final int RELATED_CATEGORY_TEXT_SCORE_THRESHOLD = 2;
    private static final int CROSS_CATEGORY_TEXT_SCORE_THRESHOLD = 4;
    private static final Set<String> STOP_WORDS = Set.of(
            "andalucia",
            "andaluz",
            "andaluza",
            "educacion",
            "educativa",
            "educativo",
            "docente",
            "docentes",
            "publica",
            "publico",
            "noticia",
            "nueva",
            "sobre",
            "desde",
            "para",
            "como",
            "esta",
            "este",
            "estos",
            "estas"
    );

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final EventRepository eventRepository;
    private final EventMatchPromptBuilder promptBuilder;
    private final EventMatchingAIProvider aiProvider;
    private final AiOperationMetricsRecorder metricsRecorder;
    private final AiModelExecutionCoordinator aiModelExecutionCoordinator;
    private final RequestImmediateAutomationWorkflowRunUseCase requestImmediateAutomationWorkflowRunUseCase;

    @Autowired
    public DetectEventUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventRepository eventRepository,
            EventMatchPromptBuilder promptBuilder,
            EventMatchingAIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder,
            AiModelExecutionCoordinator aiModelExecutionCoordinator,
            RequestImmediateAutomationWorkflowRunUseCase requestImmediateAutomationWorkflowRunUseCase
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.eventRepository = eventRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
        this.metricsRecorder = metricsRecorder;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
        this.requestImmediateAutomationWorkflowRunUseCase = requestImmediateAutomationWorkflowRunUseCase;
    }

    DetectEventUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventRepository eventRepository,
            EventMatchPromptBuilder promptBuilder,
            EventMatchingAIProvider aiProvider,
            AiOperationMetricsRecorder metricsRecorder,
            AiModelExecutionCoordinator aiModelExecutionCoordinator
    ) {
        this(newsRepository, classificationRepository, eventRepository, promptBuilder, aiProvider, metricsRecorder, aiModelExecutionCoordinator, null);
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
                .filter(event -> !event.isManualDiscarded())
                .toList();
        List<EventMatchCandidate> candidates = selectCandidates(newsArticle, category, activeEvents);
        log.info("event detection candidates loaded: newsId={}, category={}, activeEventCount={}, candidateCount={}", newsArticle.getId(), category, activeEvents.size(), candidates.size());
        EventMatchPrompt prompt = promptBuilder.build(newsArticle.getTitle(), newsArticle.getSummary(), newsArticle.getContent(), candidates);
        EventMatchingOutcome outcome;
        OffsetDateTime startedAt = metricsRecorder.start();
        try {
            outcome = aiModelExecutionCoordinator.execute("WF03_EVENT_MATCHING", () -> matchWithVerification(newsArticle, activeEvents, candidates, prompt));
        } catch (RuntimeException exception) {
            metricsRecorder.recordFailure("EVENT_MATCHING", "WF03_EVENT_MATCHING", aiProvider.providerName(), aiProvider.modelName(), "NEWS", newsArticle.getId(), startedAt, exception);
            log.error("event detection failed during AI matching: newsId={}, reason={}", newsArticle.getId(), exception.getMessage(), exception);
            throw exception;
        }
        EventMatchingAIResponse aiResponse = outcome.response();
        Event event = outcome.event();
        EventMatchDecision matchDecision = outcome.decision();
        boolean created = false;
        boolean matched = event != null;

        if (event == null) {
            event = createEvent(newsArticle, category, importanceOf(classification.getImpactLevel()));
            created = true;
            log.info("event detection creating new event: newsId={}, category={}, importance={}, matchDecision={}, confidence={}", newsArticle.getId(), category, importanceOf(classification.getImpactLevel()), matchDecision, aiResponse.confidence());
        } else {
            event.addNews(newsArticle.getId(), OffsetDateTime.now());
            log.info("event detection matched existing event: newsId={}, eventId={}, confidence={}, matchDecision={}", newsArticle.getId(), event.getId(), aiResponse.confidence(), matchDecision);
        }

        Event savedEvent = eventRepository.save(event);
        eventRepository.saveNewsAssociation(savedEvent.getId(), newsArticle.getId(), aiResponse.confidence(), matchDecision, aiResponse.reason());
        newsArticle.markEventMatched();
        newsRepository.save(newsArticle);
        requestPriorityAnalysisIfNeeded(savedEvent);

        DetectEventResult result = new DetectEventResult(
                savedEvent.getId(),
                newsArticle.getId(),
                created,
                matched,
                aiResponse.confidence(),
                aiResponse.reason(),
                savedEvent.getStatus(),
                matchDecision
        );
        metricsRecorder.recordSuccess(
                "EVENT_MATCHING",
                "WF03_EVENT_MATCHING",
                aiProvider.providerName(),
                aiProvider.modelName(),
                "NEWS",
                newsArticle.getId(),
                startedAt,
                eventDetectionDetails(newsArticle, classification, candidates, outcome, result)
        );
        log.info(
                "event detection completed: newsId={}, eventId={}, created={}, matched={}, confidence={}, status={}, matchDecision={}",
                result.newsId(),
                result.eventId(),
                result.created(),
                result.matched(),
                result.confidence(),
                result.eventStatus(),
                result.matchDecision()
        );

        return result;
    }

    private EventMatchingOutcome matchWithVerification(NewsArticle newsArticle, List<Event> activeEvents, List<EventMatchCandidate> candidates, EventMatchPrompt prompt) {
        EventMatchingAIResponse firstResponse = matchWithProvider(
                newsArticle.getId(),
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                candidates,
                prompt
        );
        Event firstMatch = findAutomaticMatch(activeEvents, candidates, firstResponse);
        if (firstMatch != null) {
            return new EventMatchingOutcome(firstResponse, firstMatch, EventMatchDecision.AUTOMATIC_MATCH, false, firstResponse);
        }

        if (!requiresSecondVerification(firstResponse, candidates)) {
            return new EventMatchingOutcome(firstResponse, null, newEventDecision(firstResponse), false, firstResponse);
        }

        List<EventMatchCandidate> verificationCandidates = verificationCandidates(candidates, firstResponse.eventId());
        EventMatchPrompt verificationPrompt = promptBuilder.build(
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                reducedContentForReviewVerification(),
                verificationCandidates
        );
        EventMatchingAIResponse secondResponse = matchWithProvider(
                newsArticle.getId(),
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                reducedContentForReviewVerification(),
                verificationCandidates,
                verificationPrompt
        );
        Event secondMatch = findAutomaticMatch(activeEvents, verificationCandidates, secondResponse);
        if (secondMatch != null) {
            return new EventMatchingOutcome(secondResponse, secondMatch, EventMatchDecision.VERIFIED_MATCH, true, firstResponse);
        }

        return new EventMatchingOutcome(secondResponse, null, EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT, true, firstResponse);
    }

    private EventMatchingAIResponse matchWithProvider(
            Long newsId,
            String newsTitle,
            String newsSummary,
            String newsContent,
            List<EventMatchCandidate> candidates,
            EventMatchPrompt prompt
    ) {
        try {
            return aiProvider.match(new EventMatchingAIRequest(
                    newsTitle,
                    newsSummary,
                    newsContent,
                    candidates,
                    prompt.systemPrompt(),
                    prompt.userPrompt()
            ));
        } catch (RuntimeException exception) {
            if (!isProviderResponseWithoutText(exception)) {
                throw exception;
            }

            log.warn("event detection retrying with reduced context after provider response without text: newsId={}, reason={}", newsId, exception.getMessage());
            String reducedContent = reducedContentForNoTextRetry();
            EventMatchPrompt reducedPrompt = promptBuilder.build(
                    newsTitle,
                    newsSummary,
                    reducedContent,
                    candidates
            );

            return aiProvider.match(new EventMatchingAIRequest(
                    newsTitle,
                    newsSummary,
                    reducedContent,
                    candidates,
                    reducedPrompt.systemPrompt(),
                    reducedPrompt.userPrompt()
            ));
        }
    }

    private boolean isProviderResponseWithoutText(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && message.startsWith("Gemini response does not contain candidates[0].content.parts[0].text");
    }

    private String reducedContentForNoTextRetry() {
        return "Contexto reducido tras respuesta IA sin texto. Decide coincidencia usando solo titulo, resumen y eventos candidatos.";
    }

    private String reducedContentForReviewVerification() {
        return "Verificacion defensiva de coincidencia dudosa. Decide solo si la noticia pertenece claramente a uno de los eventos candidatos; si hay duda, responde match=false.";
    }

    private Map<String, Object> eventDetectionDetails(
            NewsArticle newsArticle,
            NewsClassification classification,
            List<EventMatchCandidate> candidates,
            EventMatchingOutcome outcome,
            DetectEventResult result
    ) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("workflowCode", "WF03_EVENT_MATCHING");
        details.put("newsId", newsArticle.getId());
        details.put("newsTitle", abbreviate(newsArticle.getTitle()));
        details.put("category", classification.getCategory().name());
        details.put("candidateCount", candidates.size());
        details.put("candidateEventIds", candidates.stream().map(EventMatchCandidate::eventId).toList());
        details.put("aiMatch", outcome.response().match());
        details.put("aiSuggestedEventId", outcome.response().eventId());
        details.put("initialAiSuggestedEventId", outcome.initialResponse().eventId());
        details.put("initialConfidence", outcome.initialResponse().confidence());
        details.put("confidence", outcome.response().confidence());
        details.put("automaticMatchThreshold", AUTOMATIC_MATCH_THRESHOLD);
        details.put("reviewRecommendedThreshold", REVIEW_RECOMMENDED_THRESHOLD);
        details.put("decision", result.created() ? "CREATED_EVENT" : "MATCHED_EXISTING_EVENT");
        details.put("matchDecision", result.matchDecision().name());
        details.put("secondVerification", outcome.secondVerification());
        details.put("created", result.created());
        details.put("matched", result.matched());
        details.put("finalEventId", result.eventId());
        details.put("eventStatus", result.eventStatus().name());
        details.put("reason", abbreviate(outcome.response().reason()));
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

    private Event findAutomaticMatch(List<Event> activeEvents, List<EventMatchCandidate> candidates, EventMatchingAIResponse aiResponse) {
        if (!aiResponse.match() || aiResponse.confidence() < AUTOMATIC_MATCH_THRESHOLD || aiResponse.eventId() == null) {
            return null;
        }

        boolean suggestedCandidateExists = candidates.stream()
                .anyMatch(candidate -> candidate.eventId().equals(aiResponse.eventId()));
        if (!suggestedCandidateExists) {
            return null;
        }

        return activeEvents.stream()
                .filter(event -> event.getId().equals(aiResponse.eventId()))
                .findFirst()
                .orElse(null);
    }

    private boolean requiresSecondVerification(EventMatchingAIResponse response, List<EventMatchCandidate> candidates) {
        return response.confidence() >= REVIEW_RECOMMENDED_THRESHOLD
                && response.confidence() < AUTOMATIC_MATCH_THRESHOLD
                && !candidates.isEmpty();
    }

    private EventMatchDecision newEventDecision(EventMatchingAIResponse response) {
        if (response.confidence() >= REVIEW_RECOMMENDED_THRESHOLD) {
            return EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT;
        }
        return EventMatchDecision.NEW_EVENT;
    }

    private List<EventMatchCandidate> verificationCandidates(List<EventMatchCandidate> candidates, Long suggestedEventId) {
        List<EventMatchCandidate> selected = candidates.stream()
                .filter(candidate -> suggestedEventId != null && candidate.eventId().equals(suggestedEventId))
                .limit(1)
                .collect(Collectors.toList());

        candidates.stream()
                .filter(candidate -> suggestedEventId == null || !candidate.eventId().equals(suggestedEventId))
                .limit(MAX_VERIFICATION_CANDIDATES - selected.size())
                .forEach(selected::add);

        return List.copyOf(selected);
    }

    private List<EventMatchCandidate> selectCandidates(NewsArticle newsArticle, EventCategory category, List<Event> activeEvents) {
        Set<String> newsTokens = tokens(String.join(" ", safe(newsArticle.getTitle()), safe(newsArticle.getSummary()), safe(newsArticle.getContent())));

        return activeEvents.stream()
                .map(event -> scoreEvent(event, category, newsTokens))
                .filter(ScoredEventCandidate::eligible)
                .sorted(this::compareCandidates)
                .limit(MAX_CANDIDATES)
                .map(candidate -> toMatchCandidate(candidate.event()))
                .toList();
    }

    private int compareCandidates(ScoredEventCandidate left, ScoredEventCandidate right) {
        int sameCategory = Boolean.compare(right.sameCategory(), left.sameCategory());
        if (sameCategory != 0) {
            return sameCategory;
        }

        int relatedCategory = Boolean.compare(right.relatedCategory(), left.relatedCategory());
        if (relatedCategory != 0) {
            return relatedCategory;
        }

        int textScore = Integer.compare(right.textScore(), left.textScore());
        if (textScore != 0) {
            return textScore;
        }

        int lastUpdatedAt = right.event().getLastUpdatedAt().compareTo(left.event().getLastUpdatedAt());
        if (lastUpdatedAt != 0) {
            return lastUpdatedAt;
        }

        return Long.compare(right.event().getId(), left.event().getId());
    }

    private ScoredEventCandidate scoreEvent(Event event, EventCategory category, Set<String> newsTokens) {
        Set<String> eventTokens = tokens(String.join(" ", safe(event.getTitle()), safe(event.getDescription())));
        int textScore = (int) eventTokens.stream().filter(newsTokens::contains).count();
        boolean sameCategory = event.getCategory() == category;
        boolean relatedCategory = relatedCategories(category).contains(event.getCategory());
        boolean eligible = sameCategory
                || (relatedCategory && textScore >= RELATED_CATEGORY_TEXT_SCORE_THRESHOLD)
                || textScore >= CROSS_CATEGORY_TEXT_SCORE_THRESHOLD;
        return new ScoredEventCandidate(event, textScore, sameCategory, relatedCategory, eligible);
    }

    private EventMatchCandidate toMatchCandidate(Event event) {
        return new EventMatchCandidate(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getStatus(),
                event.getFirstDetectedAt(),
                event.getLastUpdatedAt(),
                event.getNewsIds().size(),
                recentNewsTitles(event)
        );
    }

    private List<String> recentNewsTitles(Event event) {
        return event.getNewsIds().stream()
                .map(newsRepository::findById)
                .flatMap(java.util.Optional::stream)
                .sorted((left, right) -> right.getCapturedAt().compareTo(left.getCapturedAt()))
                .limit(MAX_RECENT_NEWS_TITLES)
                .map(NewsArticle::getTitle)
                .toList();
    }

    private Set<EventCategory> relatedCategories(EventCategory category) {
        return switch (category) {
            case OPOSICIONES -> Set.of(EventCategory.INTERINOS, EventCategory.SIPRI, EventCategory.LEGISLACION);
            case INTERINOS -> Set.of(EventCategory.OPOSICIONES, EventCategory.SIPRI, EventCategory.PLANTILLAS);
            case SIPRI -> Set.of(EventCategory.INTERINOS, EventCategory.OPOSICIONES);
            case PLANTILLAS -> Set.of(EventCategory.INTERINOS, EventCategory.LEGISLACION);
            case RETRIBUCIONES -> Set.of(EventCategory.SINDICAL, EventCategory.CONFLICTO_LABORAL);
            case FORMACION -> Set.of(EventCategory.CURRICULO, EventCategory.DIGITALIZACION);
            case INSPECCION -> Set.of(EventCategory.LEGISLACION);
            case LEGISLACION -> Set.of(EventCategory.OPOSICIONES, EventCategory.INTERINOS, EventCategory.CURRICULO, EventCategory.INSPECCION);
            case CURRICULO -> Set.of(EventCategory.FORMACION, EventCategory.LEGISLACION, EventCategory.FP, EventCategory.DIGITALIZACION);
            case UNIVERSIDAD -> Set.of(EventCategory.FP, EventCategory.FORMACION);
            case FP -> Set.of(EventCategory.CURRICULO, EventCategory.UNIVERSIDAD);
            case DIGITALIZACION -> Set.of(EventCategory.CURRICULO, EventCategory.FORMACION);
            case INCLUSION -> Set.of(EventCategory.CURRICULO, EventCategory.INFRAESTRUCTURAS);
            case INFRAESTRUCTURAS -> Set.of(EventCategory.INCLUSION);
            case CONFLICTO_LABORAL -> Set.of(EventCategory.SINDICAL, EventCategory.RETRIBUCIONES);
            case SINDICAL -> Set.of(EventCategory.CONFLICTO_LABORAL, EventCategory.RETRIBUCIONES);
            case OTROS -> Set.of();
        };
    }

    private Set<String> tokens(String value) {
        String normalized = Normalizer.normalize(safe(value), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        return java.util.Arrays.stream(normalized.split("[^a-z0-9]+"))
                .filter(token -> token.length() >= 4)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toSet());
    }

    private String safe(String value) {
        return value == null ? "" : value;
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

    private void requestPriorityAnalysisIfNeeded(Event event) {
        if (event.getImportance() != Importance.HIGH && event.getImportance() != Importance.CRITICAL) {
            return;
        }
        if (requestImmediateAutomationWorkflowRunUseCase == null) {
            return;
        }

        boolean requested = requestImmediateAutomationWorkflowRunUseCase.execute(AutomationWorkflowCode.WF04_ANALYSIS);
        log.info("priority analysis automation requested after event detection: eventId={}, importance={}, requested={}",
                event.getId(), event.getImportance(), requested);
    }

    private record ScoredEventCandidate(Event event, int textScore, boolean sameCategory, boolean relatedCategory, boolean eligible) {
    }

    private record EventMatchingOutcome(
            EventMatchingAIResponse response,
            Event event,
            EventMatchDecision decision,
            boolean secondVerification,
            EventMatchingAIResponse initialResponse
    ) {
    }
}
