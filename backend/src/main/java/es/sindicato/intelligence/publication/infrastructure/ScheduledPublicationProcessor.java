package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.publication.application.PublishScheduledPublicationsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@ConditionalOnProperty(name = "app.publication.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduledPublicationProcessor {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPublicationProcessor.class);

    private final PublishScheduledPublicationsUseCase publishScheduledPublicationsUseCase;

    public ScheduledPublicationProcessor(PublishScheduledPublicationsUseCase publishScheduledPublicationsUseCase) {
        this.publishScheduledPublicationsUseCase = publishScheduledPublicationsUseCase;
    }

    @Scheduled(fixedDelayString = "${app.publication.scheduler.fixed-delay-ms:60000}")
    public void publishDuePublications() {
        int processed = publishScheduledPublicationsUseCase.execute(OffsetDateTime.now(), 50);
        if (processed > 0) {
            log.info("scheduled publication processor completed: processed={}", processed);
        }
    }
}
