package es.sindicato.intelligence.content.application;

public interface ContentAIProvider {

    ContentAIResponse generate(ContentAIRequest request);

    default String providerName() {
        return getClass().getSimpleName();
    }

    default String modelName() {
        return getClass().getSimpleName();
    }
}
