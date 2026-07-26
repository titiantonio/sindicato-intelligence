package es.sindicato.intelligence.event.application;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMatchPromptBuilder {

    private static final int MAX_NEWS_TITLE_LENGTH = 220;
    private static final int MAX_NEWS_SUMMARY_LENGTH = 900;
    private static final int MAX_NEWS_CONTENT_LENGTH = 1_800;
    private static final int MAX_CANDIDATE_TITLE_LENGTH = 180;
    private static final int MAX_CANDIDATE_DESCRIPTION_LENGTH = 360;
    private static final int MAX_RECENT_NEWS_TITLE_LENGTH = 140;

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
                """.formatted(
                abbreviate(newsTitle, MAX_NEWS_TITLE_LENGTH),
                abbreviate(newsSummary, MAX_NEWS_SUMMARY_LENGTH),
                compactContent(newsSummary, newsContent),
                formatCandidates(candidates)
        );

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
                        escape(abbreviate(candidate.title(), MAX_CANDIDATE_TITLE_LENGTH)),
                        escape(abbreviate(candidate.description(), MAX_CANDIDATE_DESCRIPTION_LENGTH)),
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
                .map(value -> "\"" + escape(abbreviate(value, MAX_RECENT_NEWS_TITLE_LENGTH)) + "\"")
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String compactContent(String summary, String content) {
        String safeContent = normalized(content);
        if (safeContent.isBlank()) {
            return "";
        }
        if (!normalized(summary).isBlank() && normalized(summary).equals(safeContent)) {
            return "[omitido: coincide con el resumen]";
        }
        return abbreviate(safeContent, MAX_NEWS_CONTENT_LENGTH);
    }

    private String abbreviate(String value, int maxLength) {
        String normalized = normalized(value);
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 13)).trim() + " [recortado]";
    }

    private String normalized(String value) {
        return safe(value).replaceAll("\\s+", " ").trim();
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
