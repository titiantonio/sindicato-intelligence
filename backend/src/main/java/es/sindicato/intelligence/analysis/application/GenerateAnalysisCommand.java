package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.analysis.domain.AnalysisGenerationTrigger;

public record GenerateAnalysisCommand(Long eventId, AnalysisGenerationTrigger trigger) {

    public GenerateAnalysisCommand(Long eventId) {
        this(eventId, AnalysisGenerationTrigger.MANUAL);
    }
}
