package es.sindicato.intelligence.analysis.infrastructure;

import es.sindicato.intelligence.analysis.application.AnalysisAIProvider;
import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "deterministic", matchIfMissing = true)
public class DeterministicAnalysisAIProvider implements AnalysisAIProvider {

    @Override
    public AnalysisAIResponse generate(AnalysisAIRequest request) {
        return new AnalysisAIResponse(
                "El evento agrupa informacion educativa relevante para seguimiento sindical.",
                "Conviene realizar seguimiento sindical prudente a partir de nuevas fuentes y comunicaciones oficiales.",
                List.of("Evento de categoria " + request.category(), "Noticias asociadas: " + request.news().size()),
                List.of("La informacion puede estar incompleta si las noticias asociadas no aportan contexto suficiente."),
                List.of("Monitorizar novedades oficiales y nuevas publicaciones relacionadas."),
                "deterministic-analysis"
        );
    }

    @Override
    public String modelName() {
        return "deterministic-analysis";
    }
}
