package es.sindicato.intelligence.analysis.api;

import es.sindicato.intelligence.analysis.application.AnalysisAIProviderException;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisCommand;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisUseCase;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final GenerateAnalysisUseCase generateAnalysisUseCase;

    public AnalysisController(GenerateAnalysisUseCase generateAnalysisUseCase) {
        this.generateAnalysisUseCase = generateAnalysisUseCase;
    }

    @PostMapping("/generate")
    public EventAIAnalysisResponse generateAnalysis(@Valid @RequestBody GenerateAnalysisRequest request) {
        return toResponse(generateAnalysisUseCase.execute(new GenerateAnalysisCommand(request.eventId())));
    }

    @ExceptionHandler(AnalysisAIProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handleAIProviderException(AnalysisAIProviderException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private EventAIAnalysisResponse toResponse(EventAIAnalysis analysis) {
        return new EventAIAnalysisResponse(
                analysis.getId(),
                analysis.getEventId(),
                analysis.getExecutiveSummary(),
                analysis.getUnionSummary(),
                analysis.getKeyPoints(),
                analysis.getRisks(),
                analysis.getOpportunities(),
                analysis.getModelUsed(),
                analysis.getGeneratedAt()
        );
    }
}
