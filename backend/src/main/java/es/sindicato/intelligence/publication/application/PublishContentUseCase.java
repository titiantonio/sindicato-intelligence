package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PublishContentUseCase {

    private static final Logger log = LoggerFactory.getLogger(PublishContentUseCase.class);

    private final GeneratedContentRepository contentRepository;
    private final PublicationRepository publicationRepository;
    private final List<PublishingProvider> publishingProviders;

    public PublishContentUseCase(
            GeneratedContentRepository contentRepository,
            PublicationRepository publicationRepository,
            List<PublishingProvider> publishingProviders
    ) {
        this.contentRepository = contentRepository;
        this.publicationRepository = publicationRepository;
        this.publishingProviders = publishingProviders;
    }

    @Transactional(noRollbackFor = PublishingProviderException.class)
    public Publication execute(Long contentId) {
        Objects.requireNonNull(contentId, "contentId is required");

        log.info("publication started: contentId={}", contentId);
        GeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + contentId));

        if (content.getStatus() != ContentStatus.APPROVED) {
            log.warn("publication skipped because content is not approved: contentId={}, status={}", content.getId(), content.getStatus());
            throw new IllegalStateException("only approved content can be published");
        }

        PublishingProvider publishingProvider = resolveProvider(content.getChannel());
        Publication publication = publicationRepository.save(Publication.pending(content.getId(), content.getChannel()));
        log.info("publication registered as pending: publicationId={}, contentId={}, channel={}", publication.getId(), content.getId(), content.getChannel());

        try {
            PublishingResult result = publishingProvider.publish(new PublishingRequest(
                    content.getId(),
                    content.getChannel(),
                    content.getTitle(),
                    content.getContent()
            ));

            publication.markPublished(result.externalId(), OffsetDateTime.now(), result.responsePayload());
            Publication savedPublication = publicationRepository.save(publication);
            content.markPublished();
            contentRepository.save(content);

            log.info("publication completed: publicationId={}, contentId={}, channel={}, externalId={}", savedPublication.getId(), content.getId(), savedPublication.getChannel(), savedPublication.getExternalId());
            return savedPublication;
        } catch (PublishingProviderException exception) {
            publication.markFailed(errorPayload(exception.getMessage()));
            Publication failedPublication = publicationRepository.save(publication);
            log.error("publication failed: publicationId={}, contentId={}, channel={}, reason={}", failedPublication.getId(), content.getId(), failedPublication.getChannel(), exception.getMessage(), exception);
            throw exception;
        }
    }

    private PublishingProvider resolveProvider(String channel) {
        return publishingProviders.stream()
                .filter(provider -> provider.supports(channel))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("publication skipped because channel has no provider: channel={}", channel);
                    return new IllegalStateException("publication provider not found for channel: " + channel);
                });
    }

    private String errorPayload(String message) {
        return "{\"ok\":false,\"description\":\"" + escapeJson(truncate(message)) + "\"}";
    }

    private String truncate(String value) {
        if (value == null) {
            return "publication failed";
        }
        if (value.length() <= 500) {
            return value;
        }

        return value.substring(0, 500);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
