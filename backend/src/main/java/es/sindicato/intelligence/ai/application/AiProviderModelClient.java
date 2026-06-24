package es.sindicato.intelligence.ai.application;

import java.util.List;

public interface AiProviderModelClient {

    boolean supports(String providerCode);

    List<AiModelOption> listModels(String apiKey);
}
