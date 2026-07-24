package es.sindicato.intelligence.event.application;

public enum EventEditorialStatus {
    PENDING_ANALYSIS,
    ANALYZED,
    ANALYZED_PENDING_CONTENT,
    ANALYSIS_OUTDATED,
    ANALYSIS_FAILED,
    PUBLISHED,
    DISCARDED
}
