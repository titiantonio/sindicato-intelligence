package es.sindicato.intelligence.analysis.application;

public interface AnalysisAIProvider {

    AnalysisAIResponse generate(AnalysisAIRequest request);

    default String modelName() {
        return getClass().getSimpleName();
    }
}
