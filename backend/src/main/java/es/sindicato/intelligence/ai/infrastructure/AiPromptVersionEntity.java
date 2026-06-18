package es.sindicato.intelligence.ai.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_prompt_versions")
public class AiPromptVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String promptKey;

    @Column(nullable = false)
    private String promptName;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String checksum;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected AiPromptVersionEntity() {
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
