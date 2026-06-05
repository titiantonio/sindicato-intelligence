package es.sindicato.intelligence.source.api;

import es.sindicato.intelligence.source.application.CreateSourceCommand;
import es.sindicato.intelligence.source.application.CreateSourceUseCase;
import es.sindicato.intelligence.source.application.ListSourcesUseCase;
import es.sindicato.intelligence.source.application.SourceNotFoundException;
import es.sindicato.intelligence.source.application.UpdateSourceCommand;
import es.sindicato.intelligence.source.application.UpdateSourceUseCase;
import es.sindicato.intelligence.source.domain.Source;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sources")
public class SourceController {

    private final CreateSourceUseCase createSourceUseCase;
    private final ListSourcesUseCase listSourcesUseCase;
    private final UpdateSourceUseCase updateSourceUseCase;

    public SourceController(
            CreateSourceUseCase createSourceUseCase,
            ListSourcesUseCase listSourcesUseCase,
            UpdateSourceUseCase updateSourceUseCase
    ) {
        this.createSourceUseCase = createSourceUseCase;
        this.listSourcesUseCase = listSourcesUseCase;
        this.updateSourceUseCase = updateSourceUseCase;
    }

    @GetMapping
    public List<SourceResponse> listSources() {
        return listSourcesUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<SourceResponse> createSource(@Valid @RequestBody CreateSourceRequest request) {
        Source source = createSourceUseCase.execute(new CreateSourceCommand(
                request.name(),
                request.url(),
                request.type(),
                request.priority(),
                request.active()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(source));
    }

    @PutMapping("/{id}")
    public SourceResponse updateSource(@PathVariable Long id, @Valid @RequestBody UpdateSourceRequest request) {
        Source source = updateSourceUseCase.execute(id, new UpdateSourceCommand(
                request.name(),
                request.url(),
                request.type(),
                request.priority(),
                request.active()
        ));

        return toResponse(source);
    }

    @ExceptionHandler(SourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(SourceNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    private SourceResponse toResponse(Source source) {
        return new SourceResponse(
                source.getId(),
                source.getName(),
                source.getUrl(),
                source.getType(),
                source.getPriority(),
                source.isActive(),
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }
}
