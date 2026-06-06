package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.news.application.CreateNewsCommand;
import es.sindicato.intelligence.news.application.CreateNewsUseCase;
import es.sindicato.intelligence.news.application.GetNewsUseCase;
import es.sindicato.intelligence.news.application.ListNewsUseCase;
import es.sindicato.intelligence.news.application.NewsNotFoundException;
import es.sindicato.intelligence.news.domain.NewsArticle;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    private final CreateNewsUseCase createNewsUseCase;
    private final ListNewsUseCase listNewsUseCase;
    private final GetNewsUseCase getNewsUseCase;

    public NewsController(
            CreateNewsUseCase createNewsUseCase,
            ListNewsUseCase listNewsUseCase,
            GetNewsUseCase getNewsUseCase
    ) {
        this.createNewsUseCase = createNewsUseCase;
        this.listNewsUseCase = listNewsUseCase;
        this.getNewsUseCase = getNewsUseCase;
    }

    @PostMapping
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody CreateNewsRequest request) {
        NewsArticle newsArticle = createNewsUseCase.execute(new CreateNewsCommand(
                request.sourceId(),
                request.title(),
                request.url(),
                request.summary(),
                request.content(),
                request.publishedAt()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(newsArticle));
    }

    @GetMapping
    public List<NewsResponse> listNews() {
        return listNewsUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public NewsResponse getNews(@PathVariable Long id) {
        return toResponse(getNewsUseCase.execute(id));
    }

    @ExceptionHandler(NewsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NewsNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    private NewsResponse toResponse(NewsArticle newsArticle) {
        return new NewsResponse(
                newsArticle.getId(),
                newsArticle.getSourceId(),
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                newsArticle.getHash(),
                newsArticle.getPublishedAt(),
                newsArticle.getCapturedAt(),
                newsArticle.getProcessingStatus(),
                newsArticle.getCreatedAt(),
                newsArticle.getUpdatedAt()
        );
    }
}
