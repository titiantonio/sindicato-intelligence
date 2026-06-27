package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.news.application.CreateNewsCommand;
import es.sindicato.intelligence.news.application.CreateNewsUseCase;
import es.sindicato.intelligence.event.api.EventResponseMapper;
import es.sindicato.intelligence.news.application.GetNewsTraceUseCase;
import es.sindicato.intelligence.news.application.GetNewsTraceUseCase.NewsTrace;
import es.sindicato.intelligence.news.application.GetNewsUseCase;
import es.sindicato.intelligence.news.application.IngestNewsBatchCommand;
import es.sindicato.intelligence.news.application.IngestNewsBatchResult;
import es.sindicato.intelligence.news.application.IngestNewsBatchUseCase;
import es.sindicato.intelligence.news.application.ListNewsPageUseCase;
import es.sindicato.intelligence.news.application.ListNewsUseCase;
import es.sindicato.intelligence.news.application.NewsPage;
import es.sindicato.intelligence.news.application.NewsPageItem;
import es.sindicato.intelligence.news.application.NewsPageQuery;
import es.sindicato.intelligence.news.application.NewsNotFoundException;
import es.sindicato.intelligence.news.domain.NewsArticle;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final IngestNewsBatchUseCase ingestNewsBatchUseCase;
    private final ListNewsUseCase listNewsUseCase;
    private final ListNewsPageUseCase listNewsPageUseCase;
    private final GetNewsUseCase getNewsUseCase;
    private final GetNewsTraceUseCase getNewsTraceUseCase;
    private final EventResponseMapper eventResponseMapper;

    public NewsController(
            CreateNewsUseCase createNewsUseCase,
            IngestNewsBatchUseCase ingestNewsBatchUseCase,
            ListNewsUseCase listNewsUseCase,
            ListNewsPageUseCase listNewsPageUseCase,
            GetNewsUseCase getNewsUseCase,
            GetNewsTraceUseCase getNewsTraceUseCase,
            EventResponseMapper eventResponseMapper
    ) {
        this.createNewsUseCase = createNewsUseCase;
        this.ingestNewsBatchUseCase = ingestNewsBatchUseCase;
        this.listNewsUseCase = listNewsUseCase;
        this.listNewsPageUseCase = listNewsPageUseCase;
        this.getNewsUseCase = getNewsUseCase;
        this.getNewsTraceUseCase = getNewsTraceUseCase;
        this.eventResponseMapper = eventResponseMapper;
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

    @PostMapping("/bulk")
    public IngestNewsBatchResponse ingestNewsBatch(@Valid @RequestBody List<@Valid CreateNewsRequest> request) {
        IngestNewsBatchResult result = ingestNewsBatchUseCase.execute(new IngestNewsBatchCommand(
                request.stream()
                        .map(this::toCreateCommand)
                        .toList()
        ));

        return new IngestNewsBatchResponse(
                result.totalReceived(),
                result.createdCount(),
                result.failedCount(),
                result.results().stream()
                        .map(item -> new IngestNewsBatchItemResponse(
                                item.index(),
                                item.url(),
                                item.created(),
                                item.newsId(),
                                item.error()
                        ))
                        .toList()
        );
    }

    @GetMapping
    public List<NewsResponse> listNews() {
        return listNewsUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/page")
    public NewsPageResponse listNewsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String global,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String event,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String publishedAt,
            @RequestParam(required = false) String capturedAt,
            @RequestParam(defaultValue = "capturedAt") String sortColumn,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        NewsPage newsPage = listNewsPageUseCase.execute(new NewsPageQuery(
                page,
                pageSize,
                global,
                id,
                title,
                source,
                status,
                event,
                category,
                publishedAt,
                capturedAt,
                sortColumn,
                sortDirection
        ));

        return new NewsPageResponse(
                newsPage.items().stream().map(this::toPageItemResponse).toList(),
                newsPage.page(),
                newsPage.pageSize(),
                newsPage.totalItems(),
                newsPage.totalPages()
        );
    }

    @GetMapping("/{id}")
    public NewsResponse getNews(@PathVariable Long id) {
        return toResponse(getNewsTraceUseCase.execute(id));
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

    private CreateNewsCommand toCreateCommand(CreateNewsRequest request) {
        return new CreateNewsCommand(
                request.sourceId(),
                request.title(),
                request.url(),
                request.summary(),
                request.content(),
                request.publishedAt()
        );
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

    private NewsResponse toResponse(NewsTrace trace) {
        NewsArticle newsArticle = trace.news();
        return new NewsResponse(
                newsArticle.getId(),
                newsArticle.getSourceId(),
                trace.sourceName(),
                newsArticle.getTitle(),
                newsArticle.getUrl(),
                newsArticle.getSummary(),
                newsArticle.getContent(),
                newsArticle.getHash(),
                newsArticle.getPublishedAt(),
                newsArticle.getCapturedAt(),
                newsArticle.getProcessingStatus(),
                newsArticle.getCreatedAt(),
                newsArticle.getUpdatedAt(),
                trace.event() == null ? null : trace.event().getId(),
                trace.classification() == null ? null : eventResponseMapper.toClassificationResponse(trace.classification())
        );
    }

    private NewsPageItemResponse toPageItemResponse(NewsPageItem item) {
        return new NewsPageItemResponse(
                item.id(),
                item.sourceId(),
                item.sourceName(),
                item.title(),
                item.processingStatus(),
                item.eventId(),
                item.category(),
                item.publishedAt(),
                item.capturedAt()
        );
    }
}
