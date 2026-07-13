package es.sindicato.intelligence.event.domain;

public record EventNewsAssociationTrace(
        Long newsId,
        Integer confidenceScore,
        EventMatchDecision matchDecision,
        String matchReason
) {
}
