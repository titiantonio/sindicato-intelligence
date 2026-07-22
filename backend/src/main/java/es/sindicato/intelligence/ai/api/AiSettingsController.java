package es.sindicato.intelligence.ai.api;

import es.sindicato.intelligence.ai.application.AiModelOption;
import es.sindicato.intelligence.ai.application.AiProviderSettingView;
import es.sindicato.intelligence.ai.application.AiWorkflowSettingView;
import es.sindicato.intelligence.ai.application.ListAiProviderModelsUseCase;
import es.sindicato.intelligence.ai.application.ListAiProviderSettingsUseCase;
import es.sindicato.intelligence.ai.application.ListAiWorkflowSettingsUseCase;
import es.sindicato.intelligence.ai.application.UpdateAiProviderSettingCommand;
import es.sindicato.intelligence.ai.application.UpdateAiProviderSettingUseCase;
import es.sindicato.intelligence.ai.application.UpdateAiWorkflowSettingCommand;
import es.sindicato.intelligence.ai.application.UpdateAiWorkflowSettingUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiSettingsController {

    private final ListAiProviderSettingsUseCase listAiProviderSettingsUseCase;
    private final UpdateAiProviderSettingUseCase updateAiProviderSettingUseCase;
    private final ListAiProviderModelsUseCase listAiProviderModelsUseCase;
    private final ListAiWorkflowSettingsUseCase listAiWorkflowSettingsUseCase;
    private final UpdateAiWorkflowSettingUseCase updateAiWorkflowSettingUseCase;

    public AiSettingsController(
            ListAiProviderSettingsUseCase listAiProviderSettingsUseCase,
            UpdateAiProviderSettingUseCase updateAiProviderSettingUseCase,
            ListAiProviderModelsUseCase listAiProviderModelsUseCase,
            ListAiWorkflowSettingsUseCase listAiWorkflowSettingsUseCase,
            UpdateAiWorkflowSettingUseCase updateAiWorkflowSettingUseCase
    ) {
        this.listAiProviderSettingsUseCase = listAiProviderSettingsUseCase;
        this.updateAiProviderSettingUseCase = updateAiProviderSettingUseCase;
        this.listAiProviderModelsUseCase = listAiProviderModelsUseCase;
        this.listAiWorkflowSettingsUseCase = listAiWorkflowSettingsUseCase;
        this.updateAiWorkflowSettingUseCase = updateAiWorkflowSettingUseCase;
    }

    @GetMapping("/providers")
    public List<AiProviderSettingResponse> listProviders() {
        return listAiProviderSettingsUseCase.execute().stream().map(this::toResponse).toList();
    }

    @PutMapping("/providers/{providerCode}")
    public AiProviderSettingResponse updateProvider(
            @PathVariable String providerCode,
            @Valid @RequestBody UpdateAiProviderSettingRequest request
    ) {
        return toResponse(updateAiProviderSettingUseCase.execute(
                providerCode,
                new UpdateAiProviderSettingCommand(request.enabled(), request.apiKey(), Boolean.TRUE.equals(request.clearApiKey()))
        ));
    }

    @PostMapping("/providers/{providerCode}/models")
    public List<AiModelOptionResponse> listProviderModels(
            @PathVariable String providerCode,
            @RequestBody(required = false) ListAiProviderModelsRequest request
    ) {
        String apiKey = request == null ? null : request.apiKey();
        return listAiProviderModelsUseCase.execute(providerCode, apiKey).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/workflow-settings")
    public List<AiWorkflowSettingResponse> listWorkflowSettings() {
        return listAiWorkflowSettingsUseCase.execute().stream().map(this::toResponse).toList();
    }

    @PutMapping("/workflow-settings/{workflowCode}")
    public AiWorkflowSettingResponse updateWorkflowSetting(
            @PathVariable String workflowCode,
            @Valid @RequestBody UpdateAiWorkflowSettingRequest request
    ) {
        return toResponse(updateAiWorkflowSettingUseCase.execute(
                workflowCode,
                new UpdateAiWorkflowSettingCommand(
                        request.providerCode(),
                        request.modelName(),
                        request.temperature(),
                        request.maxOutputTokens(),
                        request.cooldownSeconds()
                )
        ));
    }

    private AiProviderSettingResponse toResponse(AiProviderSettingView view) {
        return new AiProviderSettingResponse(
                view.providerCode(),
                view.displayName(),
                view.enabled(),
                view.apiKeyConfigured(),
                view.apiKeyPreview(),
                view.createdAt(),
                view.updatedAt()
        );
    }

    private AiWorkflowSettingResponse toResponse(AiWorkflowSettingView view) {
        return new AiWorkflowSettingResponse(
                view.workflowCode(),
                view.providerCode(),
                view.providerName(),
                view.modelName(),
                view.temperature(),
                view.maxOutputTokens(),
                view.cooldownSeconds(),
                view.createdAt(),
                view.updatedAt()
        );
    }

    private AiModelOptionResponse toResponse(AiModelOption model) {
        return new AiModelOptionResponse(model.name(), model.displayName());
    }
}
