package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAiProviderModelsUseCase {

    private final AiProviderSettingRepository providerRepository;
    private final List<AiProviderModelClient> modelClients;

    public ListAiProviderModelsUseCase(AiProviderSettingRepository providerRepository, List<AiProviderModelClient> modelClients) {
        this.providerRepository = providerRepository;
        this.modelClients = modelClients;
    }

    @Transactional(readOnly = true)
    public List<AiModelOption> execute(String providerCode, String apiKeyOverride) {
        String apiKey = apiKeyOverride == null || apiKeyOverride.isBlank()
                ? providerRepository.findByCode(providerCode)
                        .orElseThrow(() -> new IllegalArgumentException("ai provider setting not found: " + providerCode))
                        .getApiKey()
                : apiKeyOverride.trim();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("ai provider api key is required to list models");
        }
        AiProviderModelClient client = modelClients.stream()
                .filter(candidate -> candidate.supports(providerCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ai provider model listing not supported: " + providerCode));
        return client.listModels(apiKey);
    }
}
