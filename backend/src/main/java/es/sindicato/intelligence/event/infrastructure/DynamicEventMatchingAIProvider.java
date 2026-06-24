package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettingsResolver;
import es.sindicato.intelligence.event.application.EventMatchingAIProvider;
import es.sindicato.intelligence.event.application.EventMatchingAIRequest;
import es.sindicato.intelligence.event.application.EventMatchingAIResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class DynamicEventMatchingAIProvider implements EventMatchingAIProvider {

    private static final String WORKFLOW_CODE = "WF03_EVENT_MATCHING";

    private final AiWorkflowRuntimeSettingsResolver settingsResolver;
    private final DeterministicEventMatchingAIProvider deterministicProvider;
    private final GeminiEventMatchingAIProvider geminiProvider;

    public DynamicEventMatchingAIProvider(
            AiWorkflowRuntimeSettingsResolver settingsResolver,
            DeterministicEventMatchingAIProvider deterministicProvider,
            GeminiEventMatchingAIProvider geminiProvider
    ) {
        this.settingsResolver = settingsResolver;
        this.deterministicProvider = deterministicProvider;
        this.geminiProvider = geminiProvider;
    }

    @Override
    public EventMatchingAIResponse match(EventMatchingAIRequest request) {
        AiWorkflowRuntimeSettings settings = settingsResolver.resolve(WORKFLOW_CODE);
        if ("deterministic".equals(settings.providerCode())) {
            return deterministicProvider.match(request);
        }
        if ("gemini".equals(settings.providerCode())) {
            return geminiProvider.match(request, settings);
        }
        throw new IllegalArgumentException("unsupported ai provider for event matching: " + settings.providerCode());
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
