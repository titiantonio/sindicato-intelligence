package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.application.PublishContentUseCase;
import es.sindicato.intelligence.publication.application.PublishingProviderException;
import es.sindicato.intelligence.publication.domain.Publication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/publications")
public class PublicationController {

    private final PublishContentUseCase publishContentUseCase;

    public PublicationController(PublishContentUseCase publishContentUseCase) {
        this.publishContentUseCase = publishContentUseCase;
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
