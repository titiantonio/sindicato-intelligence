package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.application.AIProvider;
import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

@Component
public class DeterministicAIProvider implements AIProvider {

    @Override
    public ClassificationAIResponse classify(ClassificationAIRequest request) {
        String text = normalize(String.join(" ", safe(request.title()), safe(request.summary()), safe(request.content())));

        if (text.contains("sipri")) {
            return response(ClassificationCategory.SIPRI, "Adjudicaciones", 95, ImpactLevel.HIGH, UrgencyLevel.HIGH, List.of("SIPRI", "adjudicaciones"));
        }

        if (text.contains("oposicion") || text.contains("oposiciones")) {
            return response(ClassificationCategory.OPOSICIONES, "Procesos selectivos", 92, ImpactLevel.HIGH, UrgencyLevel.MEDIUM, List.of("oposiciones", "convocatoria"));
        }

        if (text.contains("interino") || text.contains("interinos")) {
            return response(ClassificationCategory.INTERINOS, "Personal interino", 88, ImpactLevel.MEDIUM, UrgencyLevel.MEDIUM, List.of("interinos"));
        }

        if (text.contains("retribucion") || text.contains("salario") || text.contains("salarios") || text.contains("nomina") || text.contains("nominas")) {
            return response(ClassificationCategory.RETRIBUCIONES, "Retribuciones docentes", 86, ImpactLevel.MEDIUM, UrgencyLevel.MEDIUM, List.of("retribuciones", "salarios"));
        }

        return response(ClassificationCategory.OTROS, "Sin clasificar", 50, ImpactLevel.LOW, UrgencyLevel.LOW, List.of("educacion"));
    }

    private ClassificationAIResponse response(
            ClassificationCategory category,
            String subcategory,
            int relevance,
            ImpactLevel impact,
            UrgencyLevel urgency,
            List<String> keywords
    ) {
        return new ClassificationAIResponse(
                category,
                subcategory,
                BigDecimal.valueOf(relevance),
                impact,
                urgency,
                keywords,
                List.of("educacion publica andaluza"),
                "Clasificacion determinista para MVP tecnico."
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }
}
