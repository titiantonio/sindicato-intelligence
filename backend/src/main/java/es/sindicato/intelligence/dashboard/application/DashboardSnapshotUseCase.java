package es.sindicato.intelligence.dashboard.application;

import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.event.application.EventEditorialStatus;
import es.sindicato.intelligence.event.application.EventEditorialStatusResolver;
import es.sindicato.intelligence.event.application.EventVisibilityPolicy;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class DashboardSnapshotUseCase {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("Europe/Madrid");
    private static final int PRIORITY_EVENTS_LIMIT = 10;

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final GeneratedContentRepository contentRepository;
    private final PublicationRepository publicationRepository;
    private final EventVisibilityPolicy eventVisibilityPolicy;
    private final EventEditorialStatusResolver editorialStatusResolver;
    private final Clock clock;

    public DashboardSnapshotUseCase(
            NewsRepository newsRepository,
            EventRepository eventRepository,
            GeneratedContentRepository contentRepository,
            PublicationRepository publicationRepository,
            EventVisibilityPolicy eventVisibilityPolicy,
            EventEditorialStatusResolver editorialStatusResolver,
            Clock clock
    ) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.contentRepository = contentRepository;
        this.publicationRepository = publicationRepository;
        this.eventVisibilityPolicy = eventVisibilityPolicy;
        this.editorialStatusResolver = editorialStatusResolver;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public DashboardSnapshot execute() {
        DateRange today = todayRange();
        DateRange yesterday = yesterdayRange(today);
        List<NewsArticle> news = newsRepository.findAll().stream()
                .filter(eventVisibilityPolicy::isVisibleNews)
                .toList();
        List<Event> events = eventRepository.findAll().stream()
                .filter(eventVisibilityPolicy::isVisible)
                .toList();
        List<Long> visibleEventIds = events.stream()
                .map(Event::getId)
                .toList();
        List<GeneratedContent> contents = contentRepository.findAll().stream()
                .filter(content -> visibleEventIds.contains(content.getEventId()))
                .toList();
        List<Long> visibleContentIds = contents.stream()
                .map(GeneratedContent::getId)
                .toList();
        List<Publication> publications = publicationRepository.findAll().stream()
                .filter(publication -> visibleContentIds.contains(publication.getContentId()))
                .toList();

        DashboardMetric capturedNews = metric("capturedNews", news, NewsArticle::getCapturedAt, today, yesterday);
        DashboardMetric detectedEvents = metric("detectedEvents", events, Event::getFirstDetectedAt, today, yesterday);
        DashboardMetric pendingContents = metric(
                "pendingContents",
                contents.stream().filter(content -> content.getStatus() == ContentStatus.PENDING_REVIEW).toList(),
                GeneratedContent::getGeneratedAt,
                today,
                yesterday
        );
        DashboardMetric publishedPublications = metric(
                "publishedPublications",
                publications.stream()
                        .filter(publication -> publication.getStatus() == PublicationStatus.PUBLISHED)
                        .filter(publication -> publication.getPublishedAt() != null)
                        .toList(),
                Publication::getPublishedAt,
                today,
                yesterday
        );
        DashboardTotals totals = new DashboardTotals(
                news.size(),
                countEvents(events, Importance.CRITICAL),
                countContents(contents, ContentStatus.PENDING_REVIEW),
                countContents(contents, ContentStatus.GENERATED),
                countContents(contents, ContentStatus.APPROVED),
                countPublications(publications, PublicationStatus.SCHEDULED),
                countPublications(publications, PublicationStatus.FAILED)
        );

        List<Event> priorityEvents = events.stream()
                .filter(this::requiresImmediateAttention)
                .sorted(Comparator
                        .comparingInt(this::importanceRank)
                        .thenComparing(Comparator.comparingInt(this::newsCount).reversed())
                        .thenComparing(Event::getLastUpdatedAt, Comparator.reverseOrder()))
                .limit(PRIORITY_EVENTS_LIMIT)
                .toList();

        DashboardLastUpdated lastUpdated = new DashboardLastUpdated(
                latestNewsUpdate(news),
                latestEventUpdate(events),
                latestContentUpdate(contents),
                latestPublicationUpdate(publications)
        );

        return new DashboardSnapshot(
                capturedNews,
                detectedEvents,
                pendingContents,
                publishedPublications,
                totals,
                lastUpdated,
                priorityEvents
        );
    }

    private <T> DashboardMetric metric(
            String key,
            List<T> items,
            Function<T, OffsetDateTime> dateExtractor,
            DateRange today,
            DateRange yesterday
    ) {
        long todayValue = countInRange(items, dateExtractor, today);
        long yesterdayValue = countInRange(items, dateExtractor, yesterday);
        return new DashboardMetric(key, todayValue, yesterdayValue, todayValue - yesterdayValue);
    }

    private <T> long countInRange(List<T> items, Function<T, OffsetDateTime> dateExtractor, DateRange range) {
        return items.stream()
                .map(dateExtractor)
                .filter(date -> date != null && range.contains(date))
                .count();
    }

    private boolean requiresImmediateAttention(Event event) {
        return event.isActive()
                && editorialStatusResolver.resolve(event) == EventEditorialStatus.PENDING_ANALYSIS
                && event.getCategory() != EventCategory.OTROS
                && (event.getImportance() == Importance.HIGH || event.getImportance() == Importance.CRITICAL);
    }

    private long countEvents(List<Event> events, Importance importance) {
        return events.stream()
                .filter(Event::isActive)
                .filter(event -> event.getImportance() == importance)
                .count();
    }

    private long countContents(List<GeneratedContent> contents, ContentStatus status) {
        return contents.stream()
                .filter(content -> content.getStatus() == status)
                .count();
    }

    private long countPublications(List<Publication> publications, PublicationStatus status) {
        return publications.stream()
                .filter(publication -> publication.getStatus() == status)
                .count();
    }

    private OffsetDateTime latestNewsUpdate(List<NewsArticle> news) {
        return latestTimestamp(news.stream()
                .flatMap(article -> Stream.of(article.getUpdatedAt(), article.getCapturedAt())));
    }

    private OffsetDateTime latestEventUpdate(List<Event> events) {
        return latestTimestamp(events.stream()
                .flatMap(event -> Stream.of(event.getUpdatedAt(), event.getLastUpdatedAt())));
    }

    private OffsetDateTime latestContentUpdate(List<GeneratedContent> contents) {
        return latestTimestamp(contents.stream()
                .flatMap(content -> Stream.of(content.getGeneratedAt(), content.getApprovedAt())));
    }

    private OffsetDateTime latestPublicationUpdate(List<Publication> publications) {
        return latestTimestamp(publications.stream()
                .flatMap(publication -> Stream.of(publication.getPublishedAt(), publication.getScheduledAt())));
    }

    private OffsetDateTime latestTimestamp(Stream<OffsetDateTime> timestamps) {
        return timestamps
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(OffsetDateTime.now(clock.withZone(DASHBOARD_ZONE)));
    }

    private int importanceRank(Event event) {
        return event.getImportance() == Importance.CRITICAL ? 0 : 1;
    }

    private int newsCount(Event event) {
        return event.getNewsIds().size();
    }

    private DateRange todayRange() {
        LocalDate today = LocalDate.now(clock.withZone(DASHBOARD_ZONE));
        OffsetDateTime start = today.atStartOfDay(DASHBOARD_ZONE).toOffsetDateTime();
        return new DateRange(start, start.plusDays(1));
    }

    private DateRange yesterdayRange(DateRange today) {
        return new DateRange(today.start().minusDays(1), today.start());
    }

    public record DashboardSnapshot(
            DashboardMetric capturedNews,
            DashboardMetric detectedEvents,
            DashboardMetric pendingContents,
            DashboardMetric publishedPublications,
            DashboardTotals totals,
            DashboardLastUpdated lastUpdated,
            List<Event> priorityEvents
    ) {
    }

    public record DashboardMetric(
            String key,
            long todayValue,
            long yesterdayValue,
            long difference
    ) {
    }

    public record DashboardTotals(
            long totalNews,
            long criticalEvents,
            long pendingContents,
            long generatedContents,
            long approvedContents,
            long scheduledPublications,
            long failedPublications
    ) {
    }

    public record DashboardLastUpdated(
            OffsetDateTime news,
            OffsetDateTime events,
            OffsetDateTime contents,
            OffsetDateTime publications
    ) {
    }

    private record DateRange(
            OffsetDateTime start,
            OffsetDateTime end
    ) {
        private boolean contains(OffsetDateTime value) {
            return !value.isBefore(start) && value.isBefore(end);
        }
    }
}
