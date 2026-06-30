package es.sindicato.intelligence.publication.application;

public interface ManualPublishingProvider {

    boolean supports(String channel);

    PublishingResult publishManual(ManualPublishingRequest request);
}
