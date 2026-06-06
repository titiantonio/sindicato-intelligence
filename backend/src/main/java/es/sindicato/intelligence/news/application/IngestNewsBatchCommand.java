package es.sindicato.intelligence.news.application;

import java.util.List;

public record IngestNewsBatchCommand(
        List<CreateNewsCommand> items
) {
}
