package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAiWorkflowSettingsUseCase {

    private final AiWorkflowSettingRepository workflowRepository;
    private final AiProviderSettingRepository providerRepository;

    public ListAiWorkflowSettingsUseCase(AiWorkflowSettingRepository workflowRepository, AiProviderSettingRepository providerRepository) {
        this.workflowRepository = workflowRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public List<AiWorkflowSettingView> execute() {
        return workflowRepository.findAll().stream().map(this::toView).toList();
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
                setting.getCooldownSeconds(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }
}
