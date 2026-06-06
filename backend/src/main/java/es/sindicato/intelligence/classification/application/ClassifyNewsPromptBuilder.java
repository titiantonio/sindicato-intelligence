package es.sindicato.intelligence.classification.application;

import org.springframework.stereotype.Component;

@Component
public class ClassifyNewsPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            Eres un analista experto en educación pública de Andalucía.

            Tu tarea consiste en analizar noticias relacionadas con:

            - Profesorado
            - Educación pública
            - Oposiciones
            - Interinos
            - SIPRI
            - Retribuciones
            - Legislación educativa
            - Formación Profesional
            - Universidad
            - Sindicatos docentes

            Debes responder EXCLUSIVAMENTE en formato JSON válido.

            Nunca añadas texto adicional.
            """;

    public ClassifyNewsPrompt build(String title, String summary, String content) {
        String userPrompt = """
                Analiza la siguiente noticia:

                TÍTULO:
                %s

                RESUMEN:
                %s

                CONTENIDO:
                %s

                Devuelve:

                {
                  "category": "",
                  "subcategory": "",
                  "relevance": 0,
                  "impact": "",
                  "urgency": "",
                  "keywords": [],
                  "entities": [],
                  "summary": ""
                }
                """.formatted(safe(title), safe(summary), safe(content));

        return new ClassifyNewsPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
