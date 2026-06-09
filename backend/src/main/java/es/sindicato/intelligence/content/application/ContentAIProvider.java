package es.sindicato.intelligence.content.application;

public interface ContentAIProvider {

    ContentAIResponse generate(ContentAIRequest request);
}
