package es.sindicato.intelligence.classification.api;

import es.sindicato.intelligence.classification.application.ClassifyNewsCommand;
import es.sindicato.intelligence.classification.application.ClassifyNewsUseCase;
import es.sindicato.intelligence.classification.domain.NewsClassification;
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
@RequestMapping("/api/v1/classifications")
public class ClassificationController {

    private final ClassifyNewsUseCase classifyNewsUseCase;

    public ClassificationController(ClassifyNewsUseCase classifyNewsUseCase) {
        this.classifyNewsUseCase = classifyNewsUseCase;
    }

    @PostMapping("/classify")
    public NewsClassificationResponse classifyNews(@Valid @RequestBody ClassifyNewsRequest request) {
        return toResponse(classifyNewsUseCase.execute(new ClassifyNewsCommand(request.newsId())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    private NewsClassificationResponse toResponse(NewsClassification classification) {
        return new NewsClassificationResponse(
                classification.getId(),
                classification.getNewsId(),
                classification.getCategory(),
                classification.getSubcategory(),
                classification.getRelevanceScore(),
                classification.getImpactLevel(),
                classification.getUrgencyLevel(),
                classification.getKeywords(),
                classification.getEntities(),
                classification.getClassifiedAt()
        );
    }
}
