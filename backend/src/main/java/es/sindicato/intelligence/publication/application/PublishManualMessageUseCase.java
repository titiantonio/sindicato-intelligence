package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.audit.application.CurrentAuditUserProvider;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationAttachmentRepository;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import es.sindicato.intelligence.publication.domain.PublicationTarget;
import es.sindicato.intelligence.publication.domain.PublicationTargetRepository;
import es.sindicato.intelligence.publication.domain.TelegramPublicationDestination;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PublishManualMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(PublishManualMessageUseCase.class);
    private static final String TELEGRAM = "TELEGRAM";

    private final PublicationRepository publicationRepository;
    private final PublicationTargetRepository targetRepository;
    private final PublicationAttachmentRepository attachmentRepository;
    private final TelegramPublicationSettingsRepository settingsRepository;
    private final PublicationAttachmentStorage attachmentStorage;
    private final List<ManualPublishingProvider> publishingProviders;
    private final CurrentAuditUserProvider currentAuditUserProvider;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public PublishManualMessageUseCase(
            PublicationRepository publicationRepository,
            PublicationTargetRepository targetRepository,
            PublicationAttachmentRepository attachmentRepository,
            TelegramPublicationSettingsRepository settingsRepository,
            PublicationAttachmentStorage attachmentStorage,
            List<ManualPublishingProvider> publishingProviders,
            CurrentAuditUserProvider currentAuditUserProvider,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.publicationRepository = publicationRepository;
        this.targetRepository = targetRepository;
        this.attachmentRepository = attachmentRepository;
        this.settingsRepository = settingsRepository;
        this.attachmentStorage = attachmentStorage;
        this.publishingProviders = publishingProviders;
        this.currentAuditUserProvider = currentAuditUserProvider;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional(noRollbackFor = PublishingProviderException.class)
    public Publication execute(PublishManualMessageCommand command) {
        Objects.requireNonNull(command, "command is required");
        validate(command);

        TelegramPublicationSettings settings = settingsRepository.find()
                .orElseThrow(() -> new IllegalStateException("telegram publication settings not found"));
        List<TelegramPublicationDestination> destinations = resolveDestinations(settings, command.destinationIds());
        Long requestedBy = currentAuditUserProvider.currentUserId().orElse(null);
        Publication publication = publicationRepository.save(Publication.manual(TELEGRAM, command.title(), command.message(), requestedBy));

        List<PublicationAttachment> attachments = attachmentStorage.store(publication.getId(), command.files()).stream()
                .map(attachmentRepository::save)
                .toList();

        ManualPublishingProvider provider = resolveProvider(command.channel());
        OffsetDateTime now = OffsetDateTime.now();
        boolean failed = false;
        java.util.ArrayList<String> externalIds = new java.util.ArrayList<>();

        log.info("manual publication started: publicationId={}, destinations={}, attachments={}", publication.getId(), destinations.size(), attachments.size());

        for (TelegramPublicationDestination destination : destinations) {
            PublicationTarget target = targetRepository.save(PublicationTarget.pending(publication.getId(), TELEGRAM, destination, now));
            try {
                PublishingResult result = provider.publishManual(new ManualPublishingRequest(
                        publication.getId(),
                        TELEGRAM,
                        command.title(),
                        command.message(),
                        target,
                        attachments
                ));
                target.markPublished(result.externalId(), result.responsePayload(), OffsetDateTime.now());
                externalIds.add(result.externalId());
                targetRepository.save(target);
            } catch (PublishingProviderException exception) {
                failed = true;
                target.markFailed(errorPayload(exception.getMessage()));
                targetRepository.save(target);
                log.error("manual publication target failed: publicationId={}, destinationId={}, reason={}", publication.getId(), destination.getId(), exception.getMessage(), exception);
            }
        }

        if (failed) {
            publication.markFailed(errorPayload("manual publication failed for one or more destinations"));
            Publication saved = publicationRepository.save(publication);
            recordAuditLogUseCase.record("MANUAL_PUBLICATION_FAILED", "PUBLICATION", saved.getId(), null, "Publicacion manual fallida #" + saved.getId());
            return saved;
        }

        publication.markPublished(String.join(",", externalIds), OffsetDateTime.now(), successPayload(externalIds));
        Publication saved = publicationRepository.save(publication);
        recordAuditLogUseCase.record("MANUAL_PUBLICATION_PUBLISHED", "PUBLICATION", saved.getId(), null, "Publicacion manual enviada #" + saved.getId());
        log.info("manual publication completed: publicationId={}, destinations={}, attachments={}", saved.getId(), destinations.size(), attachments.size());
        return saved;
    }

    private void validate(PublishManualMessageCommand command) {
        if (!TELEGRAM.equalsIgnoreCase(command.channel())) {
            throw new IllegalArgumentException("only TELEGRAM manual publications are supported");
        }
        if (command.destinationIds().isEmpty()) {
            throw new IllegalArgumentException("at least one destination is required");
        }
        boolean hasText = hasText(command.title()) || hasText(command.message());
        if (!hasText && command.files().isEmpty()) {
            throw new IllegalArgumentException("manual publication requires text or attachments");
        }
        if (command.message() != null && command.message().length() > 4096) {
            throw new IllegalArgumentException("message is too long for Telegram");
        }
    }

    private List<TelegramPublicationDestination> resolveDestinations(TelegramPublicationSettings settings, List<Long> destinationIds) {
        List<TelegramPublicationDestination> destinations = settings.activeDestinations().stream()
                .filter(destination -> destinationIds.contains(destination.getId()))
                .toList();
        if (destinations.size() != destinationIds.size()) {
            throw new IllegalArgumentException("one or more telegram destinations are not active or do not exist");
        }
        return destinations;
    }

    private ManualPublishingProvider resolveProvider(String channel) {
        return publishingProviders.stream()
                .filter(provider -> provider.supports(channel))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("manual publication provider not found for channel: " + channel));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String successPayload(List<String> externalIds) {
        return "{\"ok\":true,\"messageIds\":[\"" + String.join("\",\"", externalIds) + "\"]}";
    }

    private String errorPayload(String message) {
        String safe = message == null ? "publication failed" : message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
        return "{\"ok\":false,\"description\":\"" + safe + "\"}";
    }
}
