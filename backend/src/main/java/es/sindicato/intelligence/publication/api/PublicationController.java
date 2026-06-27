package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.application.GetPublicationUseCase;
import es.sindicato.intelligence.publication.application.GetPublicationDetailUseCase;
import es.sindicato.intelligence.publication.application.GetPublicationDetailUseCase.PublicationDetail;
import es.sindicato.intelligence.publication.application.ListPublicationsUseCase;
import es.sindicato.intelligence.publication.application.PublishContentUseCase;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.application.SchedulePublicationCommand;
import es.sindicato.intelligence.publication.application.SchedulePublicationUseCase;
import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.event.api.EventDetailResponse;
import es.sindicato.intelligence.event.api.EventResponseMapper;
import es.sindicato.intelligence.publication.domain.Publication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    private final EventResponseMapper eventResponseMapper;

    public PublicationController(
            PublishContentUseCase publishContentUseCase,
            ListPublicationsUseCase listPublicationsUseCase,
            GetPublicationUseCase getPublicationUseCase,
            GetPublicationDetailUseCase getPublicationDetailUseCase,
            SchedulePublicationUseCase schedulePublicationUseCase,
            EventResponseMapper eventResponseMapper
    ) {
        this.publishContentUseCase = publishContentUseCase;
        this.listPublicationsUseCase = listPublicationsUseCase;
        this.getPublicationUseCase = getPublicationUseCase;
        this.getPublicationDetailUseCase = getPublicationDetailUseCase;
        this.schedulePublicationUseCase = schedulePublicationUseCase;
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
        EventDetailResponse event = eventResponseMapper.toDetailResponse(detail.eventDetail());
        return new PublicationDetailResponse(
                toResponse(detail.publication()),
                toContentResponse(detail.content()),
                event
        );
    }

    @PostMapping("/{id}/publish")
    public PublicationResponse publish(@PathVariable Long id) {
        return toResponse(publishContentUseCase.execute(id));
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

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private PublicationResponse toResponse(Publication publication) {
        return new PublicationResponse(
                publication.getId(),
                publication.getContentId(),
                publication.getChannel(),
                publication.getExternalId(),
                publication.getStatus(),
                publication.getPublishedAt(),
                publication.getResponsePayload(),
                publication.getScheduledAt()
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
