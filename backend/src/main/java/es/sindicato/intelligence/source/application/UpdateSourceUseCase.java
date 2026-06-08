package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class UpdateSourceUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateSourceUseCase.class);

    private final SourceRepository sourceRepository;

    public UpdateSourceUseCase(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional
    public Source execute(Long id, UpdateSourceCommand command) {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(command, "command is required");

        log.info("source update started: sourceId={}, name='{}', url='{}', active={}", id, command.name(), command.url(), command.active());

        Source currentSource = sourceRepository.findById(id)
                .orElseThrow(() -> new SourceNotFoundException(id));

        sourceRepository.findByUrl(command.url())
                .filter(source -> !source.getId().equals(id))
                .ifPresent(source -> {
                    log.warn("source update skipped because url already exists: sourceId={}, conflictingSourceId={}, url='{}'", id, source.getId(), command.url());
                    throw new IllegalArgumentException("source url already exists");
                });

        Source updatedSource = new Source(
                currentSource.getId(),
                command.name(),
                command.url(),
                command.type(),
                command.priority(),
                command.active(),
                currentSource.getCreatedAt(),
                OffsetDateTime.now()
        );

        Source savedSource = sourceRepository.save(updatedSource);
        log.info("source update completed: sourceId={}, name='{}', active={}", savedSource.getId(), savedSource.getName(), savedSource.isActive());

        return savedSource;
    }
}
