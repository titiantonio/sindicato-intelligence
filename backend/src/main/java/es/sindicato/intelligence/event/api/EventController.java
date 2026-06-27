package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.event.application.DetectEventCommand;
import es.sindicato.intelligence.event.application.DetectEventResult;
import es.sindicato.intelligence.event.application.DetectEventUseCase;
import es.sindicato.intelligence.event.application.DiscardEventUseCase;
import es.sindicato.intelligence.event.application.EventNotFoundException;
import es.sindicato.intelligence.event.application.EventEditorialStatusResolver;
import es.sindicato.intelligence.event.application.EventSummaryView;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase.EventDetail;
import es.sindicato.intelligence.event.application.GetEventDetailUseCase.EventNewsDetail;
import es.sindicato.intelligence.event.application.ListEventsUseCase;
import es.sindicato.intelligence.event.application.MergeEventsCommand;
import es.sindicato.intelligence.event.application.MergeEventsUseCase;
import es.sindicato.intelligence.event.application.RestoreDiscardedEventUseCase;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.news.domain.NewsArticle;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final DetectEventUseCase detectEventUseCase;
    private final ListEventsUseCase listEventsUseCase;
    private final GetEventDetailUseCase getEventDetailUseCase;
    private final MergeEventsUseCase mergeEventsUseCase;
    private final DiscardEventUseCase discardEventUseCase;
    private final RestoreDiscardedEventUseCase restoreDiscardedEventUseCase;
    private final EventEditorialStatusResolver editorialStatusResolver;

    public EventController(
            DetectEventUseCase detectEventUseCase,
            ListEventsUseCase listEventsUseCase,
            GetEventDetailUseCase getEventDetailUseCase,
            MergeEventsUseCase mergeEventsUseCase,
            DiscardEventUseCase discardEventUseCase,
            RestoreDiscardedEventUseCase restoreDiscardedEventUseCase,
            EventEditorialStatusResolver editorialStatusResolver
    ) {
        this.detectEventUseCase = detectEventUseCase;
        this.listEventsUseCase = listEventsUseCase;
        this.getEventDetailUseCase = getEventDetailUseCase;
        this.mergeEventsUseCase = mergeEventsUseCase;
        this.discardEventUseCase = discardEventUseCase;
        this.restoreDiscardedEventUseCase = restoreDiscardedEventUseCase;
        this.editorialStatusResolver = editorialStatusResolver;
    }

    @GetMapping
    public List<EventSummaryResponse> listEvents() {
        return listEventsUseCase.execute().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EventDetailResponse getEvent(@PathVariable Long id) {
        return toDetailResponse(getEventDetailUseCase.execute(id));
    }

    @PostMapping("/detect")
    public DetectEventResponse detectEvent(@Valid @RequestBody DetectEventRequest request) {
        return toResponse(detectEventUseCase.execute(new DetectEventCommand(request.newsId())));
    }

    @PostMapping("/merge")
    public EventDetailResponse mergeEvents(@Valid @RequestBody MergeEventsRequest request) {
        Event mergedEvent = mergeEventsUseCase.execute(new MergeEventsCommand(
                request.targetEventId(),
                request.sourceEventIds()
        ));
        return toDetailResponse(getEventDetailUseCase.execute(mergedEvent.getId()));
    }

    @PostMapping("/{id}/discard")
    public EventSummaryResponse discardEvent(@PathVariable Long id) {
        return toSummaryResponse(discardEventUseCase.execute(id));
    }

    @PostMapping("/{id}/restore")
    public EventSummaryResponse restoreEvent(@PathVariable Long id) {
        return toSummaryResponse(restoreDiscardedEventUseCase.execute(id));
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EventNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private DetectEventResponse toResponse(DetectEventResult result) {
        return new DetectEventResponse(
                result.eventId(),
                result.newsId(),
                result.created(),
                result.matched(),
                result.confidence(),
                result.reason(),
                result.eventStatus()
        );
    }

    private EventSummaryResponse toSummaryResponse(Event event) {
        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getImportance(),
                event.getStatus(),
                editorialStatusResolver.resolve(event),
                event.getNewsIds().size(),
                event.getFirstDetectedAt(),
                event.getLastUpdatedAt(),
                event.getUpdatedAt()
        );
    }

    private EventSummaryResponse toSummaryResponse(EventSummaryView event) {
        return new EventSummaryResponse(
                event.id(),
                event.title(),
                event.description(),
                event.category(),
                event.importance(),
                event.status(),
                event.editorialStatus(),
                event.newsCount(),
                event.firstDetectedAt(),
                event.lastUpdatedAt(),
                event.updatedAt()
        );
    }

    private EventDetailResponse toDetailResponse(EventDetail detail) {
        Event event = detail.event();
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getImportance(),
                event.getStatus(),
                editorialStatusResolver.resolve(event),
                event.getNewsIds().size(),
                event.getFirstDetectedAt(),
                event.getLastUpdatedAt(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                detail.news().stream().map(this::toNewsResponse).toList(),
                detail.analyses().stream().map(this::toAnalysisResponse).toList(),
                detail.contents().stream().map(this::toContentResponse).toList()
        );
    }

    private EventNewsResponse toNewsResponse(EventNewsDetail detail) {
        NewsArticle newsArticle = detail.newsArticle();
        return new EventNewsResponse(
                newsArticle.getId(),
                newsArticle.getSourceId(),
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                newsArticle.getProcessingStatus(),
                newsArticle.getPublishedAt(),
                newsArticle.getCapturedAt(),
                detail.classification() == null ? null : toClassificationResponse(detail.classification())
        );
    }

    private EventNewsClassificationResponse toClassificationResponse(NewsClassification classification) {
        return new EventNewsClassificationResponse(
                classification.getId(),
                classification.getNewsId(),
                classification.getCategory(),
                classification.getSubcategory(),
                classification.getRelevanceScore(),
                classification.getImpactLevel(),
                classification.getUrgencyLevel(),
                classification.getKeywords(),
                classification.getEntities(),
                classification.getClassifiedAt()
        );
    }

    private EventAnalysisResponse toAnalysisResponse(EventAIAnalysis analysis) {
        return new EventAnalysisResponse(
                analysis.getId(),
                analysis.getEventId(),
                analysis.getExecutiveSummary(),
                analysis.getUnionSummary(),
                analysis.getKeyPoints(),
                analysis.getRisks(),
                analysis.getOpportunities(),
                analysis.getModelUsed(),
                analysis.getGeneratedAt()
        );
    }

    private GeneratedContentResponse toContentResponse(GeneratedContent content) {
        return new GeneratedContentResponse(
                content.getId(),
                content.getEventId(),
                content.getAnalysisId(),
                content.getCreatedBy(),
                content.getChannel(),
                content.getTone(),
                content.getTitle(),
                content.getContent(),
                content.getStatus(),
                content.getGeneratedAt(),
                content.getApprovedAt()
        );
    }
}
