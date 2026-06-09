package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RejectContentUseCase {

    private static final Logger log = LoggerFactory.getLogger(RejectContentUseCase.class);

    private final GeneratedContentRepository contentRepository;

    public RejectContentUseCase(GeneratedContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional
    public GeneratedContent execute(Long contentId) {
        Objects.requireNonNull(contentId, "contentId is required");

        log.info("content rejection started: contentId={}", contentId);
        GeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + contentId));
        content.reject();
        GeneratedContent savedContent = contentRepository.save(content);
        log.info("content rejection completed: contentId={}, eventId={}, status={}", savedContent.getId(), savedContent.getEventId(), savedContent.getStatus());

        return savedContent;
    }
}
