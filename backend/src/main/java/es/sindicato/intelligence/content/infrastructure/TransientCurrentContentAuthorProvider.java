package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.application.CurrentContentAuthorProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransientCurrentContentAuthorProvider implements CurrentContentAuthorProvider {

    private final Long defaultAuthorId;

    public TransientCurrentContentAuthorProvider(@Value("${app.content.default-created-by:1}") Long defaultAuthorId) {
        this.defaultAuthorId = defaultAuthorId;
    }

    @Override
    public Long currentAuthorId() {
        return defaultAuthorId;
    }
}
