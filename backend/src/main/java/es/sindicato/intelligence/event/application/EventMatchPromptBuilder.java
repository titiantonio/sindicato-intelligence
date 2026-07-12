package es.sindicato.intelligence.event.application;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMatchPromptBuilder {

    private static final String SYSTEM_PROMPT = """
            Eres un analista especializado en seguimiento informativo.

            Debes decidir si una noticia habla del mismo acontecimiento que alguno de los eventos existentes.

            Considera:

            - Personas
            - Organismos
            - Fechas
            - Tema principal
            - Consecuencias
            """;

    public EventMatchPrompt build(String newsTitle, String newsSummary, String newsContent, List<EventMatchCandidate> candidates) {
        String userPrompt = """
                NOTICIA NUEVA:

                TÍTULO:
                %s

                RESUMEN:
                %s

                CONTENIDO:
                %s

                EVENTOS EXISTENTES:

                %s

                Responde exclusivamente:

                {
                  "match": true,
                  "eventId": 123,
                  "confidence": 95,
                  "reason": ""
                }
                """.formatted(safe(newsTitle), safe(newsSummary), safe(newsContent), formatCandidates(candidates));

        return new EventMatchPrompt(SYSTEM_PROMPT, userPrompt);
    }

    private String formatCandidates(List<EventMatchCandidate> candidates) {
        List<EventMatchCandidate> safeCandidates = candidates == null ? List.of() : candidates;

        if (safeCandidates.isEmpty()) {
            return "[]";
        }

        return safeCandidates.stream()
                .map(candidate -> """
                        {
                          "eventId": %s,
                          "title": "%s",
                          "description": "%s",
                          "category": "%s",
                          "status": "%s",
                          "firstDetectedAt": "%s",
                          "lastUpdatedAt": "%s",
                          "newsCount": %s,
                          "recentNewsTitles": %s
                        }
                        """.formatted(
                        candidate.eventId(),
                        escape(candidate.title()),
                        escape(candidate.description()),
                        candidate.category(),
                        safeEnum(candidate.status()),
                        safe(candidate.firstDetectedAt() == null ? null : candidate.firstDetectedAt().toString()),
                        safe(candidate.lastUpdatedAt() == null ? null : candidate.lastUpdatedAt().toString()),
                        Math.max(0, candidate.newsCount()),
                        formatStringArray(candidate.recentNewsTitles())
                ))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    private String formatStringArray(List<String> values) {
        List<String> safeValues = values == null ? List.of() : values;
        return safeValues.stream()
                .map(value -> "\"" + escape(value) + "\"")
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safeEnum(Enum<?> value) {
        return value == null ? "" : value.name();
    }

    private String escape(String value) {
        return safe(value).replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
