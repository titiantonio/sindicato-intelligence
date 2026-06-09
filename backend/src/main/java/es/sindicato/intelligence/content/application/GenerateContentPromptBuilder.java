package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.event.domain.Event;
import org.springframework.stereotype.Component;

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
                request.channel(),
                request.tone(),
                request.length()
        );

        return new GenerateContentPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
