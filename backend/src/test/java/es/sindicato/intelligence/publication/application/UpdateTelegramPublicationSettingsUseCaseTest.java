package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettingsRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTelegramPublicationSettingsUseCaseTest {

    @Test
    void recordsAuditWhenTelegramSettingsAreUpdated() {
        TelegramPublicationSettingsRepository repository = mock(TelegramPublicationSettingsRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        UpdateTelegramPublicationSettingsUseCase useCase = new UpdateTelegramPublicationSettingsUseCase(repository, audit);
        TelegramPublicationSettings settings = settings(false);

        when(repository.find()).thenReturn(Optional.of(settings));
        when(repository.save(settings)).thenReturn(settings);

        TelegramPublicationSettings result = useCase.execute(new UpdateTelegramPublicationSettingsCommand(
                true,
                "https://api.telegram.org",
                "new-token",
                "chat-id",
                true
        ));

        assertEquals(true, result.isEnabled());
        verify(audit).record(eq("TELEGRAM_SETTINGS_UPDATED"), eq("TELEGRAM_SETTINGS"), eq(1L), any(), any());
    }

    @Test
    void clearsBotTokenWhenRequested() {
        TelegramPublicationSettingsRepository repository = mock(TelegramPublicationSettingsRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        UpdateTelegramPublicationSettingsUseCase useCase = new UpdateTelegramPublicationSettingsUseCase(repository, audit);
        TelegramPublicationSettings settings = settings(true);

        when(repository.find()).thenReturn(Optional.of(settings));
        when(repository.save(settings)).thenReturn(settings);

        TelegramPublicationSettings result = useCase.execute(new UpdateTelegramPublicationSettingsCommand(
                true,
                "https://api.telegram.org",
                null,
                "chat-id",
                true,
                TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_COUNT,
                TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                java.util.List.of(),
                true
        ));

        assertNull(result.getBotToken());
        verify(audit).record(eq("TELEGRAM_SETTINGS_UPDATED"), eq("TELEGRAM_SETTINGS"), eq(1L), any(), any());
    }

    private TelegramPublicationSettings settings(boolean enabled) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new TelegramPublicationSettings(
                (short) 1,
                enabled,
                "https://api.telegram.org",
                "old-token",
                "old-chat-id",
                false,
                now,
                now
        );
    }
}
