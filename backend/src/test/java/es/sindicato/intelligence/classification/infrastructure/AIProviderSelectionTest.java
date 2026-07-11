package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettings;
import es.sindicato.intelligence.ai.application.AiWorkflowRuntimeSettingsResolver;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AIProviderSelectionTest {

    @Test
    void dynamicProviderUsesDeterministicWorkflowSettings() {
        AiWorkflowRuntimeSettingsResolver resolver = mock(AiWorkflowRuntimeSettingsResolver.class);
        when(resolver.resolve("WF02_CLASSIFICATION")).thenReturn(new AiWorkflowRuntimeSettings(
                "WF02_CLASSIFICATION",
                "deterministic",
                "deterministic-classification",
                BigDecimal.valueOf(0.2),
                1024,
                60,
                null
        ));
        DynamicClassificationAIProvider provider = new DynamicClassificationAIProvider(
                resolver,
                new DeterministicAIProvider(),
                mock(GeminiAIProvider.class)
        );

        assertEquals(ClassificationCategory.SIPRI, provider.classify(request()).category());
        assertEquals("deterministic", provider.providerName());
        assertEquals("deterministic-classification", provider.modelName());
    }

    private ClassificationAIRequest request() {
        return new ClassificationAIRequest(
                "SIPRI publica adjudicaciones",
                "https://www.juntadeandalucia.es/educacion/sipri",
                "Resumen",
                "Contenido",
                "system",
                "prompt"
        );
    }
}
