package es.sindicato.intelligence.publication.application;

public interface PublishingProvider {

    boolean supports(String channel);

    PublishingResult publish(PublishingRequest request);
}
