package es.sindicato.intelligence.automation.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AutomationWorkflowSettingRepository {

    AutomationWorkflowSetting save(AutomationWorkflowSetting setting);

    List<AutomationWorkflowSetting> findAll();

    Optional<AutomationWorkflowSetting> findByCode(AutomationWorkflowCode workflowCode);

    List<AutomationWorkflowSetting> findDue(OffsetDateTime now);
}
