package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import es.sindicato.intelligence.audit.application.CurrentAuditUserProvider;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateAiWorkflowSettingUseCase {

    private final AiWorkflowSettingRepository workflowRepository;
    private final AiProviderSettingRepository providerRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentAuditUserProvider currentAuditUserProvider;

    public UpdateAiWorkflowSettingUseCase(
            AiWorkflowSettingRepository workflowRepository,
            AiProviderSettingRepository providerRepository,
            AuditLogRepository auditLogRepository,
            CurrentAuditUserProvider currentAuditUserProvider
    ) {
        this.workflowRepository = workflowRepository;
        this.providerRepository = providerRepository;
        this.auditLogRepository = auditLogRepository;
        this.currentAuditUserProvider = currentAuditUserProvider;
    }

    @Transactional
    public AiWorkflowSettingView execute(String workflowCode, UpdateAiWorkflowSettingCommand command) {
        AiWorkflowSetting setting = workflowRepository.findByWorkflowCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("ai workflow setting not found: " + workflowCode));
        if (providerRepository.findByCode(command.providerCode()).isEmpty()) {
            throw new IllegalArgumentException("ai provider setting not found: " + command.providerCode());
        }
        String oldValues = "provider=" + setting.getProviderCode() + ";model=" + setting.getModelName();
        setting.update(command.providerCode(), command.modelName(), command.temperature(), command.maxOutputTokens(), OffsetDateTime.now());
        AiWorkflowSetting saved = workflowRepository.save(setting);
        String newValues = "provider=" + saved.getProviderCode() + ";model=" + saved.getModelName();
        auditLogRepository.record(currentAuditUserProvider.currentUserId().orElse(null), "AI_WORKFLOW_SETTING_UPDATED", "AI_WORKFLOW", null, oldValues, newValues);
        return toView(saved);
    }

    private AiWorkflowSettingView toView(AiWorkflowSetting setting) {
        String providerName = providerRepository.findByCode(setting.getProviderCode())
                .map(provider -> provider.getDisplayName())
                .orElse(setting.getProviderCode());
        return new AiWorkflowSettingView(
                setting.getWorkflowCode(),
                setting.getProviderCode(),
                providerName,
                setting.getModelName(),
                setting.getTemperature(),
                setting.getMaxOutputTokens(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }
}
