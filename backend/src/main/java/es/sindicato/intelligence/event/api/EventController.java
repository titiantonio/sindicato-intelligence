package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.event.application.DetectEventCommand;
import es.sindicato.intelligence.event.application.DetectEventResult;
import es.sindicato.intelligence.event.application.DetectEventUseCase;
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
@RequestMapping("/api/v1/events")
public class EventController {

    private final DetectEventUseCase detectEventUseCase;

    public EventController(DetectEventUseCase detectEventUseCase) {
        this.detectEventUseCase = detectEventUseCase;
    }

    @PostMapping("/detect")
    public DetectEventResponse detectEvent(@Valid @RequestBody DetectEventRequest request) {
        return toResponse(detectEventUseCase.execute(new DetectEventCommand(request.newsId())));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private DetectEventResponse toResponse(DetectEventResult result) {
        return new DetectEventResponse(
                result.eventId(),
                result.newsId(),
                result.created(),
                result.matched(),
                result.confidence(),
                result.reason(),
                result.eventStatus()
        );
    }
}
