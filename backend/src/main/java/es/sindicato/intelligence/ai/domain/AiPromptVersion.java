package es.sindicato.intelligence.ai.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AiPromptVersion {

    private final Long id;
    private final String promptKey;
    private final String promptName;
    private final String module;
    private final String version;
    private final String checksum;
    private final boolean active;
    private final OffsetDateTime createdAt;

    public AiPromptVersion(
            Long id,
            String promptKey,
            String promptName,
            String module,
            String version,
            String checksum,
            boolean active,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.promptKey = Objects.requireNonNull(promptKey, "promptKey is required");
        this.promptName = Objects.requireNonNull(promptName, "promptName is required");
        this.module = Objects.requireNonNull(module, "module is required");
        this.version = Objects.requireNonNull(version, "version is required");
        this.checksum = Objects.requireNonNull(checksum, "checksum is required");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public Long getId() {
        return id;
    }

    public String getPromptKey() {
        return promptKey;
    }

    public String getPromptName() {
        return promptName;
    }

    public String getModule() {
        return module;
    }

    public String getVersion() {
        return version;
    }

    public String getChecksum() {
        return checksum;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
