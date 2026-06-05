package es.sindicato.intelligence.source.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Source {

    private final Long id;
    private final String name;
    private final String url;
    private final String type;
    private final int priority;
    private boolean active;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Source(
            Long id,
            String name,
            String url,
            String type,
            int priority,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = requireText(name, "name");
        this.url = requireText(url, "url");
        this.type = requireText(type, "type");
        this.priority = priority;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }
    }

    public void activate() {
        activate(OffsetDateTime.now());
    }

    void activate(OffsetDateTime updatedAt) {
        this.active = true;
        updateTimestamp(updatedAt);
    }

    public void deactivate() {
        deactivate(OffsetDateTime.now());
    }

    void deactivate(OffsetDateTime updatedAt) {
        this.active = false;
        updateTimestamp(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private void updateTimestamp(OffsetDateTime updatedAt) {
        Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }

        this.updatedAt = updatedAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
