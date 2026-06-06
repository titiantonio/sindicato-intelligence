package es.sindicato.intelligence.event.application;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class DetectEventUseCase {

    private static final int AUTOMATIC_MATCH_THRESHOLD = 85;

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final EventRepository eventRepository;
    private final EventMatchPromptBuilder promptBuilder;
    private final EventMatchingAIProvider aiProvider;

    public DetectEventUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventRepository eventRepository,
            EventMatchPromptBuilder promptBuilder,
            EventMatchingAIProvider aiProvider
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.eventRepository = eventRepository;
        this.promptBuilder = promptBuilder;
        this.aiProvider = aiProvider;
    }

    @Transactional
    public DetectEventResult execute(DetectEventCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.newsId(), "newsId is required");

        NewsArticle newsArticle = newsRepository.findById(command.newsId())
                .orElseThrow(() -> new IllegalArgumentException("news not found: " + command.newsId()));

        if (newsArticle.getProcessingStatus() != NewsStatus.CLASSIFIED) {
            throw new IllegalArgumentException("news must be CLASSIFIED before event detection");
        }

        if (eventRepository.existsNewsAssociation(newsArticle.getId())) {
            throw new IllegalArgumentException("news already belongs to an event");
        }

        NewsClassification classification = classificationRepository.findByNewsId(newsArticle.getId())
                .orElseThrow(() -> new IllegalArgumentException("news classification not found: " + newsArticle.getId()));

        EventCategory category = EventCategory.valueOf(classification.getCategory().name());
        List<Event> activeEvents = eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING)).stream()
                .filter(event -> event.getCategory() == category)
                .toList();
        List<EventMatchCandidate> candidates = activeEvents.stream()
                .map(event -> new EventMatchCandidate(event.getId(), event.getTitle(), event.getDescription(), event.getCategory()))
                .toList();
        EventMatchPrompt prompt = promptBuilder.build(newsArticle.getTitle(), newsArticle.getSummary(), newsArticle.getContent(), candidates);
        EventMatchingAIResponse aiResponse = aiProvider.match(new EventMatchingAIRequest(
                newsArticle.getTitle(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                candidates,
                prompt.systemPrompt(),
                prompt.userPrompt()
        ));

        Event event = findAutomaticMatch(activeEvents, aiResponse);
        boolean created = false;
        boolean matched = event != null;

        if (event == null) {
            event = createEvent(newsArticle, category, importanceOf(classification.getImpactLevel()));
            created = true;
        } else {
            event.addNews(newsArticle.getId(), OffsetDateTime.now());
        }

        Event savedEvent = eventRepository.save(event);
        eventRepository.saveNewsAssociation(savedEvent.getId(), newsArticle.getId(), aiResponse.confidence());
        newsArticle.markEventMatched();
        newsRepository.save(newsArticle);

        return new DetectEventResult(
                savedEvent.getId(),
                newsArticle.getId(),
                created,
                matched,
                aiResponse.confidence(),
                aiResponse.reason(),
                savedEvent.getStatus()
        );
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
