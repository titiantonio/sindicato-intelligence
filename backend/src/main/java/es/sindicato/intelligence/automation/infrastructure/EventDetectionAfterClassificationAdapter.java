package es.sindicato.intelligence.automation.infrastructure;

import es.sindicato.intelligence.automation.application.RequestImmediateAutomationWorkflowRunUseCase;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.classification.application.ClassifiedNewsFollowUpPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventDetectionAfterClassificationAdapter implements ClassifiedNewsFollowUpPort {

    private static final Logger log = LoggerFactory.getLogger(EventDetectionAfterClassificationAdapter.class);

    private final RequestImmediateAutomationWorkflowRunUseCase requestImmediateAutomationWorkflowRunUseCase;

    public EventDetectionAfterClassificationAdapter(RequestImmediateAutomationWorkflowRunUseCase requestImmediateAutomationWorkflowRunUseCase) {
        this.requestImmediateAutomationWorkflowRunUseCase = requestImmediateAutomationWorkflowRunUseCase;
    }

    @Override
    public void requestEventDetection(Long newsId) {
        boolean requested = requestImmediateAutomationWorkflowRunUseCase.execute(AutomationWorkflowCode.WF03_EVENT_DETECTION);
        log.info("event detection follow-up requested after classification: newsId={}, requested={}", newsId, requested);
    }
}
