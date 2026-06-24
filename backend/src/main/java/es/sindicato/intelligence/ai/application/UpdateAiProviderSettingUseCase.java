package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.audit.application.CurrentAuditUserProvider;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateAiProviderSettingUseCase {

    private final AiProviderSettingRepository repository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentAuditUserProvider currentAuditUserProvider;

    public UpdateAiProviderSettingUseCase(
            AiProviderSettingRepository repository,
            AuditLogRepository auditLogRepository,
            CurrentAuditUserProvider currentAuditUserProvider
    ) {
        this.repository = repository;
        this.auditLogRepository = auditLogRepository;
        this.currentAuditUserProvider = currentAuditUserProvider;
    }

    @Transactional
    public AiProviderSettingView execute(String providerCode, UpdateAiProviderSettingCommand command) {
        AiProviderSetting setting = repository.findByCode(providerCode)
                .orElseThrow(() -> new IllegalArgumentException("ai provider setting not found: " + providerCode));
        String oldValues = "enabled=" + setting.isEnabled() + ";apiKeyConfigured=" + setting.hasApiKey();
        setting.update(command.enabled(), command.apiKey(), command.apiKey() != null && !command.apiKey().isBlank(), OffsetDateTime.now());
        AiProviderSetting saved = repository.save(setting);
        String newValues = "enabled=" + saved.isEnabled() + ";apiKeyConfigured=" + saved.hasApiKey();
        auditLogRepository.record(currentAuditUserProvider.currentUserId().orElse(null), "AI_PROVIDER_UPDATED", "AI_PROVIDER", null, oldValues, newValues);
        return toView(saved);
    }

    private AiProviderSettingView toView(AiProviderSetting setting) {
        return new AiProviderSettingView(
                setting.getProviderCode(),
                setting.getDisplayName(),
                setting.isEnabled(),
                setting.hasApiKey(),
                setting.maskedApiKey(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }
}
