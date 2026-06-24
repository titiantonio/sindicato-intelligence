package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiWorkflowRuntimeSettingsResolver {

    private final AiWorkflowSettingRepository workflowRepository;
    private final AiProviderSettingRepository providerRepository;

    public AiWorkflowRuntimeSettingsResolver(AiWorkflowSettingRepository workflowRepository, AiProviderSettingRepository providerRepository) {
        this.workflowRepository = workflowRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public AiWorkflowRuntimeSettings resolve(String workflowCode) {
        AiWorkflowSetting workflow = workflowRepository.findByWorkflowCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("ai workflow setting not found: " + workflowCode));
        AiProviderSetting provider = providerRepository.findByCode(workflow.getProviderCode())
                .orElseThrow(() -> new IllegalArgumentException("ai provider setting not found: " + workflow.getProviderCode()));
        if (!provider.isEnabled()) {
            throw new IllegalStateException("ai provider is disabled: " + provider.getProviderCode());
        }
        return new AiWorkflowRuntimeSettings(
                workflow.getWorkflowCode(),
                provider.getProviderCode(),
                workflow.getModelName(),
                workflow.getTemperature(),
                workflow.getMaxOutputTokens(),
                provider.getApiKey()
        );
    }
}
