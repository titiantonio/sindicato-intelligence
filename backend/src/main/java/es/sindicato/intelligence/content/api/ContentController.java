package es.sindicato.intelligence.content.api;

import es.sindicato.intelligence.content.application.ApproveContentUseCase;
import es.sindicato.intelligence.content.application.ContentAIProviderException;
import es.sindicato.intelligence.content.application.EditGeneratedContentCommand;
import es.sindicato.intelligence.content.application.EditGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.GenerateContentCommand;
import es.sindicato.intelligence.content.application.GenerateContentUseCase;
import es.sindicato.intelligence.content.application.GetGeneratedContentDetailUseCase;
import es.sindicato.intelligence.content.application.GetGeneratedContentDetailUseCase.GeneratedContentDetail;
import es.sindicato.intelligence.content.application.GetGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.ListGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.RejectContentUseCase;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.event.api.EventDetailResponse;
import es.sindicato.intelligence.event.api.EventResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/content")
public class ContentController {

    private final GenerateContentUseCase generateContentUseCase;
    private final ApproveContentUseCase approveContentUseCase;
    private final RejectContentUseCase rejectContentUseCase;
    private final ListGeneratedContentUseCase listGeneratedContentUseCase;
    private final GetGeneratedContentUseCase getGeneratedContentUseCase;
    private final GetGeneratedContentDetailUseCase getGeneratedContentDetailUseCase;
    private final EditGeneratedContentUseCase editGeneratedContentUseCase;
    private final EventResponseMapper eventResponseMapper;

    public ContentController(
            GenerateContentUseCase generateContentUseCase,
            ApproveContentUseCase approveContentUseCase,
            RejectContentUseCase rejectContentUseCase,
            ListGeneratedContentUseCase listGeneratedContentUseCase,
            GetGeneratedContentUseCase getGeneratedContentUseCase,
            GetGeneratedContentDetailUseCase getGeneratedContentDetailUseCase,
            EditGeneratedContentUseCase editGeneratedContentUseCase,
            EventResponseMapper eventResponseMapper
    ) {
        this.generateContentUseCase = generateContentUseCase;
        this.approveContentUseCase = approveContentUseCase;
        this.rejectContentUseCase = rejectContentUseCase;
        this.listGeneratedContentUseCase = listGeneratedContentUseCase;
        this.getGeneratedContentUseCase = getGeneratedContentUseCase;
        this.getGeneratedContentDetailUseCase = getGeneratedContentDetailUseCase;
        this.editGeneratedContentUseCase = editGeneratedContentUseCase;
        this.eventResponseMapper = eventResponseMapper;
    }

    @GetMapping
    public List<GeneratedContentResponse> listContent() {
        return listGeneratedContentUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public GeneratedContentResponse getContent(@PathVariable Long id) {
        return toResponse(getGeneratedContentUseCase.execute(id));
    }

    @GetMapping("/{id}/detail")
    public GeneratedContentDetailResponse getContentDetail(@PathVariable Long id) {
        GeneratedContentDetail detail = getGeneratedContentDetailUseCase.execute(id);
        EventDetailResponse event = eventResponseMapper.toDetailResponse(detail.eventDetail());
        return new GeneratedContentDetailResponse(toResponse(detail.content()), event);
    }

    @PostMapping("/generate")
    public GeneratedContentResponse generateContent(@Valid @RequestBody GenerateContentRequest request) {
        return toResponse(generateContentUseCase.execute(new GenerateContentCommand(
                request.eventId(),
                request.analysisId(),
                request.channel(),
                request.tone(),
                request.length()
        )));
    }

    @PostMapping("/{id}/approve")
    public GeneratedContentResponse approveContent(@PathVariable Long id) {
        return toResponse(approveContentUseCase.execute(id));
    }

    @PostMapping("/{id}/reject")
    public GeneratedContentResponse rejectContent(@PathVariable Long id) {
        return toResponse(rejectContentUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public GeneratedContentResponse editContent(
            @PathVariable Long id,
            @Valid @RequestBody EditGeneratedContentRequest request
    ) {
        return toResponse(editGeneratedContentUseCase.execute(new EditGeneratedContentCommand(
                id,
                request.title(),
                request.content(),
                request.tone()
        )));
    }

    @ExceptionHandler(ContentAIProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handleAIProviderException(ContentAIProviderException exception) {
        return Map.of("error", "AI provider request failed");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private GeneratedContentResponse toResponse(GeneratedContent content) {
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
