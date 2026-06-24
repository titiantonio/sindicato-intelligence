package es.sindicato.intelligence.event.application;

public interface EventMatchingAIProvider {

    EventMatchingAIResponse match(EventMatchingAIRequest request);

    default String providerName() {
        return getClass().getSimpleName();
    }

    default String modelName() {
        return getClass().getSimpleName();
    }
}
