package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CreateSourceUseCase {

    private final SourceRepository sourceRepository;

    public CreateSourceUseCase(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional
    public Source execute(CreateSourceCommand command) {
        Objects.requireNonNull(command, "command is required");

        sourceRepository.findByUrl(command.url()).ifPresent(source -> {
            throw new IllegalArgumentException("source url already exists");
        });

        OffsetDateTime now = OffsetDateTime.now();
        Source source = new Source(
                null,
                command.name(),
                command.url(),
                command.type(),
                command.priority(),
                command.active(),
                now,
                now
        );

        return sourceRepository.save(source);
    }
}
