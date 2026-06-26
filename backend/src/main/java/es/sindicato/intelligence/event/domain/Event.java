package es.sindicato.intelligence.event.domain;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Event {

    private final Long id;
    private final String title;
    private final String description;
    private final EventCategory category;
    private final Importance importance;
    private EventStatus status;
    private boolean manualDiscarded;
    private OffsetDateTime manualDiscardedAt;
    private final Set<Long> newsIds;
    private final OffsetDateTime firstDetectedAt;
    private OffsetDateTime lastUpdatedAt;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Event(
            Long id,
            String title,
            String description,
            EventCategory category,
            Importance importance,
            EventStatus status,
            Set<Long> newsIds,
            OffsetDateTime firstDetectedAt,
            OffsetDateTime lastUpdatedAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(
                id,
                title,
                description,
                category,
                importance,
                status,
                false,
                null,
                newsIds,
                firstDetectedAt,
                lastUpdatedAt,
                createdAt,
                updatedAt
        );
    }

    public Event(
            Long id,
            String title,
            String description,
            EventCategory category,
            Importance importance,
            EventStatus status,
            boolean manualDiscarded,
            OffsetDateTime manualDiscardedAt,
            Set<Long> newsIds,
            OffsetDateTime firstDetectedAt,
            OffsetDateTime lastUpdatedAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.title = requireText(title, "title");
        this.description = description;
        this.category = Objects.requireNonNull(category, "category is required");
        this.importance = Objects.requireNonNull(importance, "importance is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.manualDiscarded = manualDiscarded;
        this.manualDiscardedAt = manualDiscardedAt;
        this.newsIds = new LinkedHashSet<>(requireNewsIds(newsIds, this.status));
        this.firstDetectedAt = Objects.requireNonNull(firstDetectedAt, "firstDetectedAt is required");
        this.lastUpdatedAt = Objects.requireNonNull(lastUpdatedAt, "lastUpdatedAt is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (lastUpdatedAt.isBefore(firstDetectedAt)) {
            throw new IllegalArgumentException("lastUpdatedAt cannot be before firstDetectedAt");
        }

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }

        if (manualDiscarded && manualDiscardedAt == null) {
            throw new IllegalArgumentException("manualDiscardedAt is required when event is manually discarded");
        }

        if (!manualDiscarded && manualDiscardedAt != null) {
            throw new IllegalArgumentException("manualDiscardedAt must be null when event is not manually discarded");
        }
    }

    public void addNews(Long newsId, OffsetDateTime updatedAt) {
        ensureActive();

        if (newsIds.add(requireNewsId(newsId))) {
            updateActivity(updatedAt);
        }
    }

    public void removeNews(Long newsId, OffsetDateTime updatedAt) {
        Long validNewsId = requireNewsId(newsId);

        if (!newsIds.contains(validNewsId)) {
            return;
        }

        if (newsIds.size() == 1) {
            throw new IllegalStateException("event must have at least one news article");
        }

        newsIds.remove(validNewsId);
        updateActivity(updatedAt);
    }

    public void markMonitoring(OffsetDateTime updatedAt) {
        changeStatus(EventStatus.MONITORING, updatedAt);
    }

    public void close(OffsetDateTime updatedAt) {
        changeStatus(EventStatus.CLOSED, updatedAt);
    }

    public void reopen(OffsetDateTime updatedAt) {
        changeStatus(EventStatus.OPEN, updatedAt);
    }

    public void archive(OffsetDateTime updatedAt) {
        changeStatus(EventStatus.ARCHIVED, updatedAt);
    }

    public void markManuallyDiscarded(OffsetDateTime discardedAt) {
        Objects.requireNonNull(discardedAt, "discardedAt is required");
        if (!isActive()) {
            throw new IllegalStateException("only active events can be discarded");
        }

        this.manualDiscarded = true;
        this.manualDiscardedAt = discardedAt;
        updateActivity(discardedAt);
    }

    public void restoreManualDiscard(OffsetDateTime restoredAt) {
        Objects.requireNonNull(restoredAt, "restoredAt is required");
        if (!manualDiscarded) {
            throw new IllegalStateException("only manually discarded events can be restored");
        }

        this.manualDiscarded = false;
        this.manualDiscardedAt = null;
        updateActivity(restoredAt);
    }

    public boolean isActive() {
        return status == EventStatus.OPEN || status == EventStatus.MONITORING;
    }

    public boolean isManualDiscarded() {
        return manualDiscarded;
    }

    public OffsetDateTime getManualDiscardedAt() {
        return manualDiscardedAt;
    }

    void changeStatus(EventStatus status, OffsetDateTime updatedAt) {
        this.status = Objects.requireNonNull(status, "status is required");
        updateActivity(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventCategory getCategory() {
        return category;
    }

    public Importance getImportance() {
        return importance;
    }

    public EventStatus getStatus() {
        return status;
    }

    public Set<Long> getNewsIds() {
        return Set.copyOf(newsIds);
    }

    public OffsetDateTime getFirstDetectedAt() {
        return firstDetectedAt;
    }

    public OffsetDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Event withNewsIds(Set<Long> newsIds, OffsetDateTime updatedAt) {
        return new Event(
                id,
                title,
                description,
                category,
                importance,
                status,
                manualDiscarded,
                manualDiscardedAt,
                newsIds,
                firstDetectedAt,
                updatedAt,
                createdAt,
                updatedAt
        );
    }

    public Event archivedWithoutNews(OffsetDateTime updatedAt) {
        return new Event(
                id,
                title,
                description,
                category,
                importance,
                EventStatus.ARCHIVED,
                manualDiscarded,
                manualDiscardedAt,
                Set.of(),
                firstDetectedAt,
                updatedAt,
                createdAt,
                updatedAt
        );
    }

    private static Set<Long> requireNewsIds(Set<Long> newsIds, EventStatus status) {
        Objects.requireNonNull(newsIds, "newsIds is required");

        if (newsIds.isEmpty() && status != EventStatus.ARCHIVED) {
            throw new IllegalArgumentException("event must have at least one news article");
        }

        if (newsIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("newsIds cannot contain null values");
        }

        return Set.copyOf(newsIds);
    }

    private void ensureActive() {
        if (!isActive()) {
            throw new IllegalStateException("closed or archived events do not accept new news articles");
        }
    }

    private void updateActivity(OffsetDateTime updatedAt) {
        Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (updatedAt.isBefore(firstDetectedAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before firstDetectedAt");
        }

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }

        this.lastUpdatedAt = updatedAt;
        this.updatedAt = updatedAt;
    }

    private static Long requireNewsId(Long newsId) {
        return Objects.requireNonNull(newsId, "newsId is required");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
