package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAiProviderSettingsUseCase {

    private final AiProviderSettingRepository repository;

    public ListAiProviderSettingsUseCase(AiProviderSettingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AiProviderSettingView> execute() {
        return repository.findAll().stream().map(this::toView).toList();
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
