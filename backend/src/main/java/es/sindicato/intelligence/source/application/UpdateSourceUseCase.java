package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class UpdateSourceUseCase {

    private final SourceRepository sourceRepository;

    public UpdateSourceUseCase(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional
    public Source execute(Long id, UpdateSourceCommand command) {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(command, "command is required");

        Source currentSource = sourceRepository.findById(id)
                .orElseThrow(() -> new SourceNotFoundException(id));

        sourceRepository.findByUrl(command.url())
                .filter(source -> !source.getId().equals(id))
                .ifPresent(source -> {
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

        return sourceRepository.save(updatedSource);
    }
}
