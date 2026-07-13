package es.sindicato.intelligence.analysis.application;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateAnalysisPromptBuilder {

    private static final int MAX_TITLE_LENGTH = 300;
    private static final int MAX_SUMMARY_LENGTH = 900;
    private static final int MAX_CONTENT_LENGTH = 1_500;
    private static final int MAX_NEWS_CONTEXT_LENGTH = 12_000;

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
            6. Escribe frases cortas y evita repeticiones.
            7. No mezcles idiomas: responde siempre en espanol.
            8. Si el contexto esta recortado, trabaja solo con el texto disponible y declara la limitacion.
            """;

    public GenerateAnalysisPrompt build(AnalysisAIRequest request) {
        String userPrompt = """
                EVENTO:
                id: %s
                titulo: %s
                descripcion: %s
                categoria: %s
                importancia: %s
                tipo_analisis: %s

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
                - executiveSummary: 1 o 2 frases, maximo 280 caracteres en total.
                - unionSummary: 1 o 2 frases, maximo 420 caracteres, lectura sindical prudente, sin llamar a acciones no justificadas por los datos.
                - keyPoints: 2 a 5 items, maximo 180 caracteres por item, solo hechos verificables deducidos de las noticias.
                - risks: 0 a 4 items, maximo 180 caracteres por item, indicando incertidumbre si faltan datos.
                - opportunities: 0 a 4 items, maximo 180 caracteres por item, oportunidades de seguimiento, comunicacion o analisis sindical.
                - affectedGroups: 0 a 5 items, maximo 120 caracteres por item, colectivos afectados si se mencionan o se deducen claramente.
                - recommendedMonitoring: 1 a 4 items, maximo 180 caracteres por item, aspectos concretos a vigilar en proximas noticias o fuentes oficiales.

                Reglas por tipo de analisis:
                - CRISIS: prioriza impacto inmediato, incertidumbres, colectivos afectados y seguimiento urgente.
                - PRIORITY: prioriza lectura sindical y riesgos operativos sin exagerar.
                - STANDARD: sintetiza hechos y seguimiento normal.
                - QUICK: genera un analisis breve para decidir si merece seguimiento adicional.

                Si la informacion es limitada, no rellenes con suposiciones: explica la limitacion de forma breve dentro del JSON.
                No repitas palabras o fragmentos. No uses ingles salvo nombres propios o siglas presentes en las noticias.
                """.formatted(
                request.eventId(),
                safe(request.eventTitle()),
                safe(request.eventDescription()),
                request.category(),
                request.importance(),
                request.analysisType(),
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
                    .append("  fuente: ").append(safe(item.sourceName())).append('\n')
                    .append("  prioridad_fuente: ").append(item.sourcePriority() == null ? "" : item.sourcePriority()).append('\n')
                    .append("  titulo: ").append(limit(item.title(), MAX_TITLE_LENGTH)).append('\n')
                    .append("  url: ").append(limit(item.url(), MAX_TITLE_LENGTH)).append('\n')
                    .append("  resumen: ").append(limit(item.summary(), MAX_SUMMARY_LENGTH)).append('\n')
                    .append("  contenido: ").append(limit(item.content(), MAX_CONTENT_LENGTH)).append('\n')
                    .append("  publicado: ").append(item.publishedAt()).append("\n\n");
            if (builder.length() >= MAX_NEWS_CONTEXT_LENGTH) {
                builder.append("Contexto adicional omitido por limite operativo del prompt.");
                break;
            }
        }

        return limit(builder.toString().trim(), MAX_NEWS_CONTEXT_LENGTH);
    }

    public boolean isContextTruncated(List<AnalysisNewsItem> news) {
        String context = newsContext(news);
        return context.contains("[recortado]") || context.contains("Contexto adicional omitido");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String limit(String value, int maxLength) {
        String safeValue = safe(value).replaceAll("\\s+", " ").trim();
        if (safeValue.length() <= maxLength) {
            return safeValue;
        }

        return safeValue.substring(0, maxLength - 15).trim() + " [recortado]";
    }
}
