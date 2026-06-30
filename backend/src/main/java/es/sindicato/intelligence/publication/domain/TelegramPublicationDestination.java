package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class TelegramPublicationDestination {

    private final Long id;
    private final String name;
    private final String chatId;
    private final boolean active;
    private final boolean defaultSelected;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public TelegramPublicationDestination(
            Long id,
            String name,
            String chatId,
            boolean active,
            boolean defaultSelected,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.name = requireText(name, "destination name is required");
        this.chatId = requireText(chatId, "destination chatId is required");
        this.active = active;
        this.defaultSelected = defaultSelected;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public static TelegramPublicationDestination newDestination(String name, String chatId, boolean active, boolean defaultSelected, OffsetDateTime now) {
        return new TelegramPublicationDestination(null, name, chatId, active, defaultSelected, now, now);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDefaultSelected() {
        return defaultSelected;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
