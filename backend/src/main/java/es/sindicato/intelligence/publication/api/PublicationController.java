package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.application.GetPublicationUseCase;
import es.sindicato.intelligence.publication.application.ListPublicationsUseCase;
import es.sindicato.intelligence.publication.application.PublishContentUseCase;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.domain.Publication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public PublicationController(
            PublishContentUseCase publishContentUseCase,
            ListPublicationsUseCase listPublicationsUseCase,
            GetPublicationUseCase getPublicationUseCase
    ) {
        this.publishContentUseCase = publishContentUseCase;
        this.listPublicationsUseCase = listPublicationsUseCase;
        this.getPublicationUseCase = getPublicationUseCase;
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

    @PostMapping("/{id}/publish")
    public PublicationResponse publish(@PathVariable Long id) {
        return toResponse(publishContentUseCase.execute(id));
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
                publication.getResponsePayload()
        );
    }
}