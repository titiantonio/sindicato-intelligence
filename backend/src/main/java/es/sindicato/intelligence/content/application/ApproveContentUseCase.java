package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class ApproveContentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveContentUseCase.class);

    private final GeneratedContentRepository contentRepository;

    public ApproveContentUseCase(GeneratedContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional
    public GeneratedContent execute(Long contentId) {
        Objects.requireNonNull(contentId, "contentId is required");

        log.info("content approval started: contentId={}", contentId);
        GeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found: " + contentId));
        content.approve(OffsetDateTime.now());
        GeneratedContent savedContent = contentRepository.save(content);
        log.info("content approval completed: contentId={}, eventId={}, status={}", savedContent.getId(), savedContent.getEventId(), savedContent.getStatus());

        return savedContent;
    }
}
