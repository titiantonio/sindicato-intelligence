package es.sindicato.intelligence.ai.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AiProviderSetting {

    private final String providerCode;
    private final String displayName;
    private boolean enabled;
    private String apiKey;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public AiProviderSetting(String providerCode, String displayName, boolean enabled, String apiKey, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.providerCode = requireText(providerCode, "providerCode is required");
        this.displayName = requireText(displayName, "displayName is required");
        this.enabled = enabled;
        this.apiKey = normalize(apiKey);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public void update(boolean enabled, String apiKey, boolean replaceApiKey, OffsetDateTime now) {
        update(enabled, apiKey, replaceApiKey, false, now);
    }

    public void update(boolean enabled, String apiKey, boolean replaceApiKey, boolean clearApiKey, OffsetDateTime now) {
        this.enabled = enabled;
        if (clearApiKey) {
            this.apiKey = null;
        } else if (replaceApiKey) {
            this.apiKey = normalize(apiKey);
        }
        this.updatedAt = Objects.requireNonNull(now, "now is required");
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String maskedApiKey() {
        if (!hasApiKey()) {
            return null;
        }
        String trimmed = apiKey.trim();
        if (trimmed.length() <= 8) {
            return "********";
        }
        return trimmed.substring(0, 4) + "..." + trimmed.substring(trimmed.length() - 4);
    }

    public String getProviderCode() {
        return providerCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
