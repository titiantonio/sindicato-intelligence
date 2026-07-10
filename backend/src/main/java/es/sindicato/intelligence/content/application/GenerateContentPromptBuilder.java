package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.event.domain.Event;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateContentPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            Eres redactor de comunicacion institucional de un sindicato docente andaluz.

            El tono debe ser:
            - Informativo.
            - Profesional.
            - Neutral.
            - Claro.

            Reglas estrictas:
            1. No exageres.
            2. No utilices lenguaje sensacionalista.
            3. No inventes datos, fechas, cifras, convocatorias ni consecuencias no presentes en el evento o analisis.
            4. Genera un borrador listo para revision humana, no para publicacion automatica.
            5. Responde exclusivamente con JSON valido.
            6. Si se aportan enlaces relevantes permitidos, incluye al menos uno en el mensaje cuando aporte contexto directo al evento.
            """;

    public GenerateContentPrompt build(ContentAIRequest request) {
        Event event = request.event();
        EventAIAnalysis analysis = request.analysis();

        String userPrompt = """
                EVENTO:
                id: %s
                titulo: %s
                descripcion: %s
                categoria: %s
                importancia: %s

                ANALISIS:
                resumen ejecutivo: %s
                resumen sindical: %s
                puntos clave: %s
                riesgos: %s
                oportunidades: %s

                ENLACES RELEVANTES PERMITIDOS:
                %s

                PARAMETROS:
                canal: %s
                tono: %s
                longitud: %s

                Genera un objeto JSON con exactamente esta estructura:
                {
                  "title": "",
                  "message": "",
                  "hashtags": []
                }

                Criterios para Telegram:
                - Longitud STANDARD: 150-400 palabras.
                - Longitud SHORT: 50-100 palabras.
                - El mensaje debe ser claro, revisable y sin afirmaciones no respaldadas.
                - Si ENLACES RELEVANTES PERMITIDOS contiene documentos, consultas, listados, anexos o paginas oficiales utiles, incluye el enlace mas relevante de forma natural en el mensaje.
                - No inventes enlaces ni incluyas enlaces que no esten en ENLACES RELEVANTES PERMITIDOS.
                - Incluye hashtags utiles y prudentes, sin saturar el mensaje.
                - No incluyas markdown ni explicaciones fuera del JSON.
                """.formatted(
                event.getId(),
                safe(event.getTitle()),
                safe(event.getDescription()),
                event.getCategory(),
                event.getImportance(),
                safe(analysis.getExecutiveSummary()),
                safe(analysis.getUnionSummary()),
                analysis.getKeyPoints(),
                analysis.getRisks(),
                analysis.getOpportunities(),
                relevantLinksContext(request.relevantLinks()),
                request.channel(),
                request.tone(),
                request.length()
        );

        return new GenerateContentPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String relevantLinksContext(List<RelevantContentLink> links) {
        if (links == null || links.isEmpty()) {
            return "Sin enlaces relevantes permitidos.";
        }

        StringBuilder builder = new StringBuilder();
        for (RelevantContentLink link : links.stream().limit(5).toList()) {
            builder.append("- noticiaId: ").append(link.newsId()).append('\n')
                    .append("  etiqueta: ").append(safe(link.label())).append('\n')
                    .append("  url: ").append(safe(link.url())).append("\n");
        }
        return builder.toString().trim();
    }
}
