package es.sindicato.intelligence.news.application;

import org.springframework.stereotype.Component;

@Component
public class NewsCaptureNormalizer {

    public CreateNewsCommand normalize(CreateNewsCommand command) {
        return new CreateNewsCommand(
                command.sourceId(),
                normalizeText(command.title()),
                normalizeText(command.url()),
                normalizeOptionalText(command.summary()),
                normalizeOptionalText(command.content()),
                command.publishedAt()
        );
    }

    private String normalizeOptionalText(String value) {
        String normalized = normalizeText(value);
        return normalized == null || normalized.isEmpty() ? null : normalized;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().replaceAll("\\s+", " ");
    }
}
