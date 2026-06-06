package es.sindicato.intelligence.event.application;

public interface EventMatchingAIProvider {

    EventMatchingAIResponse match(EventMatchingAIRequest request);
}
