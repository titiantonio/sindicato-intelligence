package es.sindicato.intelligence.classification.application;

public interface AIProvider {

    ClassificationAIResponse classify(ClassificationAIRequest request);

    default String providerName() {
        return getClass().getSimpleName();
    }

    default String modelName() {
        return getClass().getSimpleName();
    }
}
