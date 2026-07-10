package es.sindicato.intelligence.source.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CreateSourceUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateSourceUseCase.class);

    private final SourceRepository sourceRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public CreateSourceUseCase(SourceRepository sourceRepository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.sourceRepository = sourceRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public Source execute(CreateSourceCommand command) {
        Objects.requireNonNull(command, "command is required");

        log.info("source creation started: name='{}', url='{}', type={}, active={}", command.name(), command.url(), command.type(), command.active());

        sourceRepository.findByUrl(command.url()).ifPresent(source -> {
            log.warn("source creation skipped because url already exists: url='{}', existingSourceId={}", command.url(), source.getId());
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

        Source savedSource = sourceRepository.save(source);
        recordAuditLogUseCase.record(
                "SOURCE_CREATED",
                "SOURCE",
                savedSource.getId(),
                null,
                AuditDetailFormatter.sourceCreated(savedSource.getId(), savedSource.getName(), savedSource.getType(), savedSource.getPriority(), savedSource.isActive())
        );
        log.info("source creation completed: sourceId={}, name='{}', active={}", savedSource.getId(), savedSource.getName(), savedSource.isActive());

        return savedSource;
    }
}
