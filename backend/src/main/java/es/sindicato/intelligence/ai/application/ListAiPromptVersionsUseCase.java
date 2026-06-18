package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiPromptVersion;
import es.sindicato.intelligence.ai.domain.AiPromptVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAiPromptVersionsUseCase {

    private final AiPromptVersionRepository repository;

    public ListAiPromptVersionsUseCase(AiPromptVersionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AiPromptVersion> execute() {
        return repository.findActive();
    }
}
