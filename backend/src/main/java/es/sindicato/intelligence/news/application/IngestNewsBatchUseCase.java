package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class IngestNewsBatchUseCase {

    private final CreateNewsUseCase createNewsUseCase;
    private final NewsCaptureNormalizer newsCaptureNormalizer;
    private final NewsHashGenerator newsHashGenerator;

    public IngestNewsBatchUseCase(
            CreateNewsUseCase createNewsUseCase,
            NewsCaptureNormalizer newsCaptureNormalizer,
            NewsHashGenerator newsHashGenerator
    ) {
        this.createNewsUseCase = createNewsUseCase;
        this.newsCaptureNormalizer = newsCaptureNormalizer;
        this.newsHashGenerator = newsHashGenerator;
    }

    public IngestNewsBatchResult execute(IngestNewsBatchCommand command) {
        Objects.requireNonNull(command, "command is required");
        Objects.requireNonNull(command.items(), "items are required");

        if (command.items().isEmpty()) {
            throw new IllegalArgumentException("news batch cannot be empty");
        }

        List<IngestNewsBatchItemResult> itemResults = new ArrayList<>();
        int createdCount = 0;
        Set<String> seenUrlsInBatch = new HashSet<>();
        Set<String> seenHashesInBatch = new HashSet<>();

        for (int index = 0; index < command.items().size(); index++) {
            CreateNewsCommand createNewsCommand = newsCaptureNormalizer.normalize(command.items().get(index));
            String hash = newsHashGenerator.calculate(createNewsCommand);

            if (!seenUrlsInBatch.add(createNewsCommand.url())) {
                itemResults.add(new IngestNewsBatchItemResult(
                        index,
                        createNewsCommand.url(),
                        false,
                        null,
                        "news url duplicated in batch"
                ));
                continue;
            }

            if (!seenHashesInBatch.add(hash)) {
                itemResults.add(new IngestNewsBatchItemResult(
                        index,
                        createNewsCommand.url(),
                        false,
                        null,
                        "news hash duplicated in batch"
                ));
                continue;
            }

            try {
                NewsArticle createdNews = createNewsUseCase.execute(createNewsCommand);
                createdCount++;
                itemResults.add(new IngestNewsBatchItemResult(
                        index,
                        createNewsCommand.url(),
                        true,
                        createdNews.getId(),
                        null
                ));
            } catch (RuntimeException exception) {
                itemResults.add(new IngestNewsBatchItemResult(
                        index,
                        createNewsCommand.url(),
                        false,
                        null,
                        exception.getMessage()
                ));
            }
        }

        int total = command.items().size();
        return new IngestNewsBatchResult(total, createdCount, total - createdCount, itemResults);
    }
}
