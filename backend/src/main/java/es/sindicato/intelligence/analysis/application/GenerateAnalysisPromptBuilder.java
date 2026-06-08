package es.sindicato.intelligence.analysis.application;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateAnalysisPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            Eres un analista senior especializado en educacion publica andaluza.

            Debes analizar toda la informacion disponible sobre un evento.

            Tu analisis debe ser:
            - Objetivo.
            - Neutral.
            - Basado en hechos proporcionados.
            - Orientado a responsables sindicales.

            Reglas estrictas:
            1. Responde exclusivamente con un objeto JSON valido.
            2. No incluyas markdown, explicaciones externas ni texto fuera del JSON.
            3. No inventes informacion, fechas, cifras, actores, intenciones ni consecuencias no presentes en el evento o las noticias.
            4. Si un dato no esta disponible, indicalo como limitacion dentro del resumen o del seguimiento recomendado.
            5. Manten tono profesional, prudente e informativo.
            """;

    public GenerateAnalysisPrompt build(AnalysisAIRequest request) {
        String userPrompt = """
                EVENTO:
                id: %s
                titulo: %s
                descripcion: %s
                categoria: %s
                importancia: %s

                NOTICIAS:
                %s

                Genera un objeto JSON con exactamente esta estructura:
                {
                  "executiveSummary": "",
                  "unionSummary": "",
                  "keyPoints": [],
                  "risks": [],
                  "opportunities": [],
                  "affectedGroups": [],
                  "recommendedMonitoring": []
                }

                Criterios:
                - executiveSummary: maximo dos frases, orientadas a comprension rapida del evento.
                - unionSummary: lectura sindical prudente, sin llamar a acciones no justificadas por los datos.
                - keyPoints: hechos verificables deducidos solo de las noticias.
                - risks: riesgos potenciales para seguimiento sindical, indicando incertidumbre si faltan datos.
                - opportunities: oportunidades de seguimiento, comunicacion o analisis sindical.
                - affectedGroups: colectivos afectados si se mencionan o se deducen claramente.
                - recommendedMonitoring: aspectos concretos a vigilar en proximas noticias o fuentes oficiales.

                Si la informacion es limitada, no rellenes con suposiciones: explica la limitacion de forma breve dentro del JSON.
                """.formatted(
                request.eventId(),
                safe(request.eventTitle()),
                safe(request.eventDescription()),
                request.category(),
                request.importance(),
                newsContext(request.news())
        );

        return new GenerateAnalysisPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String newsContext(List<AnalysisNewsItem> news) {
        if (news == null || news.isEmpty()) {
            return "Sin noticias asociadas.";
        }

        StringBuilder builder = new StringBuilder();
        for (AnalysisNewsItem item : news) {
            builder.append("- id: ").append(item.id()).append('\n')
                    .append("  titulo: ").append(safe(item.title())).append('\n')
                    .append("  resumen: ").append(safe(item.summary())).append('\n')
                    .append("  contenido: ").append(safe(item.content())).append('\n')
                    .append("  publicado: ").append(item.publishedAt()).append("\n\n");
        }

        return builder.toString().trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
