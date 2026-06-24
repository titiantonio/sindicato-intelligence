package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettingsResolver;
import es.sindicato.intelligence.content.application.ContentAIProvider;
import es.sindicato.intelligence.content.application.ContentAIRequest;
import es.sindicato.intelligence.content.application.ContentAIResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class DynamicContentAIProvider implements ContentAIProvider {

    private static final String WORKFLOW_CODE = "WF05_CONTENT";

    private final AiWorkflowRuntimeSettingsResolver settingsResolver;
    private final DeterministicContentAIProvider deterministicProvider;
    private final GeminiContentAIProvider geminiProvider;

    public DynamicContentAIProvider(
            AiWorkflowRuntimeSettingsResolver settingsResolver,
            DeterministicContentAIProvider deterministicProvider,
            GeminiContentAIProvider geminiProvider
    ) {
        this.settingsResolver = settingsResolver;
        this.deterministicProvider = deterministicProvider;
        this.geminiProvider = geminiProvider;
    }

    @Override
    public ContentAIResponse generate(ContentAIRequest request) {
        AiWorkflowRuntimeSettings settings = settingsResolver.resolve(WORKFLOW_CODE);
        if ("deterministic".equals(settings.providerCode())) {
            return deterministicProvider.generate(request);
        }
        if ("gemini".equals(settings.providerCode())) {
            return geminiProvider.generate(request, settings);
        }
        throw new IllegalArgumentException("unsupported ai provider for content: " + settings.providerCode());
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
