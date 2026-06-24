package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.application.ContentAIProvider;
import es.sindicato.intelligence.content.application.ContentAIRequest;
import es.sindicato.intelligence.content.application.ContentAIResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeterministicContentAIProvider implements ContentAIProvider {

    @Override
    public ContentAIResponse generate(ContentAIRequest request) {
        return new ContentAIResponse(
                request.event().getTitle(),
                "Resumen para revision: " + request.analysis().getExecutiveSummary() + "\n\n" + request.analysis().getUnionSummary(),
                List.of("#EducacionPublica", "#Andalucia")
        );
    }

    @Override
    public String providerName() {
        return "deterministic";
    }

    @Override
    public String modelName() {
        return "deterministic-content";
    }
}
