package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettingsResolver;
import es.sindicato.intelligence.classification.application.AIProvider;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class DynamicClassificationAIProvider implements AIProvider {

    private static final String WORKFLOW_CODE = "WF02_CLASSIFICATION";

    private final AiWorkflowRuntimeSettingsResolver settingsResolver;
    private final DeterministicAIProvider deterministicProvider;
    private final GeminiAIProvider geminiProvider;

    public DynamicClassificationAIProvider(
            AiWorkflowRuntimeSettingsResolver settingsResolver,
            DeterministicAIProvider deterministicProvider,
            GeminiAIProvider geminiProvider
    ) {
        this.settingsResolver = settingsResolver;
        this.deterministicProvider = deterministicProvider;
        this.geminiProvider = geminiProvider;
    }

    @Override
    public ClassificationAIResponse classify(ClassificationAIRequest request) {
        AiWorkflowRuntimeSettings settings = settingsResolver.resolve(WORKFLOW_CODE);
        if ("deterministic".equals(settings.providerCode())) {
            return deterministicProvider.classify(request);
        }
        if ("gemini".equals(settings.providerCode())) {
            return geminiProvider.classify(request, settings);
        }
        throw new IllegalArgumentException("unsupported ai provider for classification: " + settings.providerCode());
    }

    @Override
    public String providerName() {
        return safeSettings().providerCode();
    }

    @Override
    public String modelName() {
        return safeSettings().modelName();
    }

    private AiWorkflowRuntimeSettings safeSettings() {
        try {
            return settingsResolver.resolve(WORKFLOW_CODE);
        } catch (RuntimeException exception) {
            return new AiWorkflowRuntimeSettings(WORKFLOW_CODE, "unresolved", "unresolved", null, 1, null);
        }
    }
}
