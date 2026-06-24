package es.sindicato.intelligence.analysis.infrastructure;

import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettingsResolver;
import es.sindicato.intelligence.analysis.application.AnalysisAIProvider;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class DynamicAnalysisAIProvider implements AnalysisAIProvider {

    private static final String WORKFLOW_CODE = "WF04_ANALYSIS";

    private final AiWorkflowRuntimeSettingsResolver settingsResolver;
    private final DeterministicAnalysisAIProvider deterministicProvider;
    private final GeminiAnalysisAIProvider geminiProvider;

    public DynamicAnalysisAIProvider(
            AiWorkflowRuntimeSettingsResolver settingsResolver,
            DeterministicAnalysisAIProvider deterministicProvider,
            GeminiAnalysisAIProvider geminiProvider
    ) {
        this.settingsResolver = settingsResolver;
        this.deterministicProvider = deterministicProvider;
        this.geminiProvider = geminiProvider;
    }

    @Override
    public AnalysisAIResponse generate(AnalysisAIRequest request) {
        AiWorkflowRuntimeSettings settings = settingsResolver.resolve(WORKFLOW_CODE);
        if ("deterministic".equals(settings.providerCode())) {
            return deterministicProvider.generate(request);
        }
        if ("gemini".equals(settings.providerCode())) {
            return geminiProvider.generate(request, settings);
        }
        throw new IllegalArgumentException("unsupported ai provider for analysis: " + settings.providerCode());
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
