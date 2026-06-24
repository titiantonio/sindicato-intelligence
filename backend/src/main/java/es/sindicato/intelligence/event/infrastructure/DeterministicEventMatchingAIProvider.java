package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.application.EventMatchCandidate;
import es.sindicato.intelligence.event.application.EventMatchingAIProvider;
import es.sindicato.intelligence.event.application.EventMatchingAIRequest;
import es.sindicato.intelligence.event.application.EventMatchingAIResponse;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DeterministicEventMatchingAIProvider implements EventMatchingAIProvider {

    private static final int AUTOMATIC_MATCH_THRESHOLD = 85;
    private static final Set<String> STOP_WORDS = Set.of(
            "andalucia",
            "docente",
            "docentes",
            "educacion",
            "publica",
            "noticia",
            "nueva",
            "sobre",
            "desde",
            "para"
    );

    @Override
    public EventMatchingAIResponse match(EventMatchingAIRequest request) {
        String newsText = normalize(String.join(" ", safe(request.newsTitle()), safe(request.newsSummary()), safe(request.newsContent())));
        Set<String> newsTokens = tokens(newsText);

        EventMatchCandidate bestCandidate = null;
        int bestConfidence = 0;

        for (EventMatchCandidate candidate : request.candidates()) {
            int confidence = confidence(newsText, newsTokens, candidate);

            if (confidence > bestConfidence) {
                bestCandidate = candidate;
                bestConfidence = confidence;
            }
        }

        if (bestCandidate != null && bestConfidence >= AUTOMATIC_MATCH_THRESHOLD) {
            return new EventMatchingAIResponse(true, bestCandidate.eventId(), bestConfidence, "Coincidencia determinista de hechos para MVP tecnico.");
        }

        return new EventMatchingAIResponse(false, null, bestConfidence, "No hay coincidencia suficiente para asociacion automatica.");
    }

    @Override
    public String providerName() {
        return "deterministic";
    }

    @Override
    public String modelName() {
        return "deterministic-event-matching";
    }

    private int confidence(String newsText, Set<String> newsTokens, EventMatchCandidate candidate) {
        String candidateText = normalize(String.join(" ", safe(candidate.title()), safe(candidate.description()), candidate.category().name()));
        Set<String> candidateTokens = tokens(candidateText);
        long commonTokens = candidateTokens.stream().filter(newsTokens::contains).count();

        int score = (int) Math.min(80, commonTokens * 20);
        String categoryToken = normalize(candidate.category().name());

        if (newsText.contains(categoryToken)) {
            score += 20;
        }

        return Math.min(100, score);
    }

    private Set<String> tokens(String value) {
        return Arrays.stream(value.split("[^a-z0-9]+"))
                .filter(token -> token.length() >= 4)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toSet());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }
}
