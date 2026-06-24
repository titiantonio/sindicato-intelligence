package es.sindicato.intelligence.ai.application;

public record UpdateAiProviderSettingCommand(
        boolean enabled,
        String apiKey
) {
}
