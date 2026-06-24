package es.sindicato.intelligence.ai.domain;

import java.util.List;
import java.util.Optional;

public interface AiProviderSettingRepository {

    AiProviderSetting save(AiProviderSetting setting);

    List<AiProviderSetting> findAll();

    Optional<AiProviderSetting> findByCode(String providerCode);
}
