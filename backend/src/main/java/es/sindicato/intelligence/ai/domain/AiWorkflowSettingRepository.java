package es.sindicato.intelligence.ai.domain;

import java.util.List;
import java.util.Optional;

public interface AiWorkflowSettingRepository {

    AiWorkflowSetting save(AiWorkflowSetting setting);

    List<AiWorkflowSetting> findAll();

    Optional<AiWorkflowSetting> findByWorkflowCode(String workflowCode);
}
