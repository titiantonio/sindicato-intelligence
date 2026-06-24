package es.sindicato.intelligence.ai.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_provider_settings")
public class AiProviderSettingEntity {

    @Id
    @Column(name = "provider_code", nullable = false, length = 50)
    private String providerCode;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "api_key_encrypted", columnDefinition = "TEXT")
    private String apiKeyEncrypted;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AiProviderSettingEntity() {
    }

    public AiProviderSettingEntity(String providerCode, String displayName, boolean enabled, String apiKeyEncrypted, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.providerCode = providerCode;
        this.displayName = displayName;
        this.enabled = enabled;
        this.apiKeyEncrypted = apiKeyEncrypted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getApiKeyEncrypted() {
        return apiKeyEncrypted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
