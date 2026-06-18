package es.sindicato.intelligence.content.application;

public interface ContentAIProvider {

    ContentAIResponse generate(ContentAIRequest request);

    default String modelName() {
        return getClass().getSimpleName();
    }
}
