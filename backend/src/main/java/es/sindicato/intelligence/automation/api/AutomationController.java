package es.sindicato.intelligence.automation.api;

import es.sindicato.intelligence.automation.application.AutomationRunError;
import es.sindicato.intelligence.automation.application.AutomationRunResult;
import es.sindicato.intelligence.automation.application.AutomationOverview;
import es.sindicato.intelligence.automation.application.GetAutomationOverviewUseCase;
import es.sindicato.intelligence.automation.application.GetAutomationSettingUseCase;
import es.sindicato.intelligence.automation.application.ListAutomationSettingsUseCase;
import es.sindicato.intelligence.automation.application.ProcessPendingEventAnalysisUseCase;
import es.sindicato.intelligence.automation.application.RunPendingAnalysisCommand;
import es.sindicato.intelligence.automation.application.RunAutomationWorkflowUseCase;
import es.sindicato.intelligence.automation.application.UpdateAutomationSettingUseCase;
import es.sindicato.intelligence.automation.application.UpdateAutomationWorkflowSettingCommand;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/automation")
public class AutomationController {

    private final ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase;
    private final ListAutomationSettingsUseCase listAutomationSettingsUseCase;
    private final GetAutomationSettingUseCase getAutomationSettingUseCase;
    private final UpdateAutomationSettingUseCase updateAutomationSettingUseCase;
    private final RunAutomationWorkflowUseCase runAutomationWorkflowUseCase;
    private final GetAutomationOverviewUseCase getAutomationOverviewUseCase;

    public AutomationController(
            ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase,
            ListAutomationSettingsUseCase listAutomationSettingsUseCase,
            GetAutomationSettingUseCase getAutomationSettingUseCase,
            UpdateAutomationSettingUseCase updateAutomationSettingUseCase,
            RunAutomationWorkflowUseCase runAutomationWorkflowUseCase,
            GetAutomationOverviewUseCase getAutomationOverviewUseCase
    ) {
        this.processPendingEventAnalysisUseCase = processPendingEventAnalysisUseCase;
        this.listAutomationSettingsUseCase = listAutomationSettingsUseCase;
        this.getAutomationSettingUseCase = getAutomationSettingUseCase;
        this.updateAutomationSettingUseCase = updateAutomationSettingUseCase;
        this.runAutomationWorkflowUseCase = runAutomationWorkflowUseCase;
        this.getAutomationOverviewUseCase = getAutomationOverviewUseCase;
    }

    @PostMapping("/classifications/run")
    public AutomationRunResponse runClassifications() {
        return toResponse(runAutomationWorkflowUseCase.execute(AutomationWorkflowCode.WF02_CLASSIFICATION));
    }

    @PostMapping("/events/run")
    public AutomationRunResponse runEvents() {
        return toResponse(runAutomationWorkflowUseCase.execute(AutomationWorkflowCode.WF03_EVENT_DETECTION));
    }

    @PostMapping("/analysis/run")
    public AutomationRunResponse runAnalysis(@RequestBody(required = false) RunAnalysisAutomationRequest request) {
        Long eventId = request == null ? null : request.eventId();
        if (eventId == null) {
            return toResponse(runAutomationWorkflowUseCase.execute(AutomationWorkflowCode.WF04_ANALYSIS));
        }
        return toResponse(processPendingEventAnalysisUseCase.execute(new RunPendingAnalysisCommand(eventId)));
    }

    @GetMapping("/settings")
    public List<AutomationWorkflowSettingResponse> listSettings() {
        return listAutomationSettingsUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/overview")
    public AutomationOverviewResponse overview() {
        AutomationOverview overview = getAutomationOverviewUseCase.execute();
        return new AutomationOverviewResponse(
                overview.n8nWorkflowCode(),
                overview.n8nWorkflowName(),
                overview.n8nStatus(),
                overview.backendEnabledCount(),
                overview.backendFailedCount(),
                overview.backendRunningCount(),
                overview.backendWorkflows().stream().map(this::toResponse).toList()
        );
    }

    @GetMapping("/settings/{workflowCode}")
    public AutomationWorkflowSettingResponse getSetting(@PathVariable AutomationWorkflowCode workflowCode) {
        return toResponse(getAutomationSettingUseCase.execute(workflowCode));
    }

    @PutMapping("/settings/{workflowCode}")
    public AutomationWorkflowSettingResponse updateSetting(
            @PathVariable AutomationWorkflowCode workflowCode,
            @Valid @RequestBody UpdateAutomationSettingRequest request
    ) {
        return toResponse(updateAutomationSettingUseCase.execute(
                workflowCode,
                new UpdateAutomationWorkflowSettingCommand(request.enabled(), request.intervalSeconds(), request.batchSize())
        ));
    }

    @PostMapping("/settings/{workflowCode}/run")
    public AutomationRunResponse runSettingWorkflow(@PathVariable AutomationWorkflowCode workflowCode) {
        return toResponse(runAutomationWorkflowUseCase.execute(workflowCode));
    }

    private AutomationRunResponse toResponse(AutomationRunResult result) {
        return new AutomationRunResponse(
                result.processedCount(),
                result.successCount(),
                result.failedCount(),
                result.skippedCount(),
                result.errors().stream().map(this::toResponse).toList()
        );
    }

    private AutomationRunErrorResponse toResponse(AutomationRunError error) {
        return new AutomationRunErrorResponse(error.entityId(), error.message());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException exception) {
        return Map.of("error", exception.getMessage());
    }

    private AutomationWorkflowSettingResponse toResponse(AutomationWorkflowSetting setting) {
        return new AutomationWorkflowSettingResponse(
                setting.getWorkflowCode().name(),
                setting.isEnabled(),
                setting.getIntervalSeconds(),
                setting.getBatchSize(),
                setting.isRunning(),
                setting.getLastRunAt(),
                setting.getLastSuccessAt(),
                setting.getLastFailureAt(),
                setting.getNextRunAt(),
                setting.getLastProcessedCount(),
                setting.getLastSuccessCount(),
                setting.getLastFailedCount(),
                setting.getLastSkippedCount(),
                setting.getLastError()
        );
    }
}
