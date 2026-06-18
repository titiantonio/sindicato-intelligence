package es.sindicato.intelligence.ai.domain;

import java.util.List;

public interface AiPromptVersionRepository {

    List<AiPromptVersion> findActive();
}
