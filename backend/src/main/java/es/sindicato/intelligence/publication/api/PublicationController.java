package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.event.api.EventDetailResponse;
import es.sindicato.intelligence.event.api.EventResponseMapper;
import es.sindicato.intelligence.publication.application.GetPublicationDetailUseCase;
import es.sindicato.intelligence.publication.application.GetPublicationDetailUseCase.PublicationDetail;
import es.sindicato.intelligence.publication.application.GetPublicationUseCase;
import es.sindicato.intelligence.publication.application.ListPublicationsUseCase;
import es.sindicato.intelligence.publication.application.ManualPublicationFile;
import es.sindicato.intelligence.publication.application.ManualPublicationValidationException;
import es.sindicato.intelligence.publication.application.PublishContentUseCase;
import es.sindicato.intelligence.publication.application.PublishManualMessageCommand;
import es.sindicato.intelligence.publication.application.PublishManualMessageUseCase;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.application.SchedulePublicationCommand;
import es.sindicato.intelligence.publication.application.SchedulePublicationUseCase;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationAttachmentRepository;
import es.sindicato.intelligence.publication.domain.PublicationTarget;
import es.sindicato.intelligence.publication.domain.PublicationTargetRepository;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/publications")
public class PublicationController {

    private final PublishContentUseCase publishContentUseCase;
    private final ListPublicationsUseCase listPublicationsUseCase;
    private final GetPublicationUseCase getPublicationUseCase;
    private final GetPublicationDetailUseCase getPublicationDetailUseCase;
    private final SchedulePublicationUseCase schedulePublicationUseCase;
    private final PublishManualMessageUseCase publishManualMessageUseCase;
    private final PublicationTargetRepository targetRepository;
    private final PublicationAttachmentRepository attachmentRepository;
    private final TelegramPublicationSettingsRepository telegramSettingsRepository;
    private final UserRepository userRepository;
    private final EventResponseMapper eventResponseMapper;

    public PublicationController(
            PublishContentUseCase publishContentUseCase,
            ListPublicationsUseCase listPublicationsUseCase,
            GetPublicationUseCase getPublicationUseCase,
            GetPublicationDetailUseCase getPublicationDetailUseCase,
            SchedulePublicationUseCase schedulePublicationUseCase,
            PublishManualMessageUseCase publishManualMessageUseCase,
            PublicationTargetRepository targetRepository,
            PublicationAttachmentRepository attachmentRepository,
            TelegramPublicationSettingsRepository telegramSettingsRepository,
            UserRepository userRepository,
            EventResponseMapper eventResponseMapper
    ) {
        this.publishContentUseCase = publishContentUseCase;
        this.listPublicationsUseCase = listPublicationsUseCase;
        this.getPublicationUseCase = getPublicationUseCase;
        this.getPublicationDetailUseCase = getPublicationDetailUseCase;
        this.schedulePublicationUseCase = schedulePublicationUseCase;
        this.publishManualMessageUseCase = publishManualMessageUseCase;
        this.targetRepository = targetRepository;
        this.attachmentRepository = attachmentRepository;
        this.telegramSettingsRepository = telegramSettingsRepository;
        this.userRepository = userRepository;
        this.eventResponseMapper = eventResponseMapper;
    }

    @GetMapping
    public List<PublicationResponse> listPublications() {
        return listPublicationsUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PublicationResponse getPublication(@PathVariable Long id) {
        return toResponse(getPublicationUseCase.execute(id));
    }

    @GetMapping("/{id}/detail")
    public PublicationDetailResponse getPublicationDetail(@PathVariable Long id) {
        PublicationDetail detail = getPublicationDetailUseCase.execute(id);
        EventDetailResponse event = detail.eventDetail() == null ? null : eventResponseMapper.toDetailResponse(detail.eventDetail());
        return new PublicationDetailResponse(
                toResponse(detail.publication()),
                detail.content() == null ? null : toContentResponse(detail.content()),
                event
        );
    }

    @GetMapping("/telegram-destinations")
    public List<OperationalTelegramDestinationResponse> listTelegramDestinations() {
        return telegramSettingsRepository.find()
                .orElseThrow(() -> new IllegalStateException("telegram publication settings not found"))
                .activeDestinations()
                .stream()
                .filter(destination -> destination.getId() != null)
                .map(destination -> new OperationalTelegramDestinationResponse(
                        destination.getId(),
                        destination.getName(),
                        destination.isDefaultSelected()
                ))
                .toList();
    }

    @PostMapping("/{id}/publish")
    public PublicationResponse publish(@PathVariable Long id) {
        return toResponse(publishContentUseCase.execute(id));
    }

    @PostMapping(path = "/manual", consumes = "multipart/form-data")
    public PublicationResponse publishManual(
            @RequestParam String channel,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String message,
            @RequestParam List<Long> destinationIds,
            @RequestParam(required = false, name = "files") List<MultipartFile> files
    ) {
        return toResponse(publishManualMessageUseCase.execute(new PublishManualMessageCommand(
                channel,
                title,
                message,
                destinationIds,
                toManualFiles(files)
        )));
    }

    @PostMapping("/{id}/schedule")
    public PublicationResponse schedule(
            @PathVariable Long id,
            @Valid @RequestBody SchedulePublicationRequest request
    ) {
        return toResponse(schedulePublicationUseCase.execute(new SchedulePublicationCommand(id, request.scheduledAt())));
    }

    @ExceptionHandler(PublishingProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handlePublishingProviderException(PublishingProviderException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(ManualPublicationValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleManualPublicationValidationException(ManualPublicationValidationException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private PublicationResponse toResponse(Publication publication) {
        UserAccount requestedBy = publication.getRequestedBy() == null
                ? null
                : userRepository.findById(publication.getRequestedBy()).orElse(null);
        return new PublicationResponse(
                publication.getId(),
                publication.getContentId(),
                publication.getChannel(),
                publication.getPublicationType(),
                publication.getTitleSnapshot(),
                publication.getMessageSnapshot(),
                publication.getRequestedBy(),
                requestedBy == null ? null : requestedBy.getName(),
                requestedBy == null ? null : requestedBy.getEmail(),
                publication.getExternalId(),
                publication.getStatus(),
                publication.getPublishedAt(),
                publication.getResponsePayload(),
                publication.getScheduledAt(),
                targetRepository.findByPublicationId(publication.getId()).stream().map(this::toTargetResponse).toList(),
                attachmentRepository.findByPublicationId(publication.getId()).stream().map(this::toAttachmentResponse).toList()
        );
    }

    private List<ManualPublicationFile> toManualFiles(List<MultipartFile> files) {
        if (files == null) {
            return List.of();
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(this::toManualFile)
                .toList();
    }

    private ManualPublicationFile toManualFile(MultipartFile file) {
        try {
            return new ManualPublicationFile(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (IOException exception) {
            throw new IllegalArgumentException("attachment cannot be read: " + file.getOriginalFilename(), exception);
        }
    }

    private PublicationTargetResponse toTargetResponse(PublicationTarget target) {
        return new PublicationTargetResponse(
                target.getId(),
                target.getDestinationId(),
                target.getDestinationName(),
                target.getStatus(),
                target.getExternalId(),
                target.getResponsePayload(),
                target.getPublishedAt()
        );
    }

    private PublicationAttachmentResponse toAttachmentResponse(PublicationAttachment attachment) {
        return new PublicationAttachmentResponse(
                attachment.getId(),
                attachment.getOriginalFilename(),
                attachment.getMediaType(),
                attachment.getMimeType(),
                attachment.getFileSizeBytes(),
                attachment.getTelegramMethod(),
                attachment.getPosition()
        );
    }

    private GeneratedContentResponse toContentResponse(GeneratedContent content) {
        return new GeneratedContentResponse(
                content.getId(),
                content.getEventId(),
                content.getAnalysisId(),
                content.getCreatedBy(),
                content.getChannel(),
                content.getTone(),
                content.getTitle(),
                content.getContent(),
                content.getStatus(),
                content.getGeneratedAt(),
                content.getApprovedAt()
        );
    }
}
