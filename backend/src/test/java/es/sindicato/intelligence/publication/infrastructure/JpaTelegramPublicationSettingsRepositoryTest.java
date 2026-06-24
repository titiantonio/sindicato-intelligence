package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.core.security.SecretTextCipher;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.env.MockEnvironment;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaTelegramPublicationSettingsRepositoryTest {

    @Test
    void encryptsBotTokenBeforeSavingAndReturnsPlainDomainValue() {
        EntityManager entityManager = mock(EntityManager.class);
        SecretTextCipher cipher = new SecretTextCipher("local-settings-encryption-key-32-bytes-min", new MockEnvironment());
        JpaTelegramPublicationSettingsRepository repository = new JpaTelegramPublicationSettingsRepository(entityManager, cipher);
        OffsetDateTime now = OffsetDateTime.parse("2026-06-24T10:00:00Z");
        TelegramPublicationSettings settings = new TelegramPublicationSettings(
                (short) 1,
                true,
                "https://api.telegram.org",
                "telegram-token",
                "chat-id",
                true,
                now,
                now
        );
        when(entityManager.merge(any(TelegramPublicationSettingsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TelegramPublicationSettings saved = repository.save(settings);

        ArgumentCaptor<TelegramPublicationSettingsEntity> captor = ArgumentCaptor.forClass(TelegramPublicationSettingsEntity.class);
        verify(entityManager).merge(captor.capture());
        assertNotEquals("telegram-token", captor.getValue().getBotToken());
        assertTrue(captor.getValue().getBotToken().startsWith("enc:v1:"));
        assertEquals("telegram-token", saved.getBotToken());
    }

    @Test
    void decryptsEncryptedBotTokenWhenReading() {
        EntityManager entityManager = mock(EntityManager.class);
        SecretTextCipher cipher = new SecretTextCipher("local-settings-encryption-key-32-bytes-min", new MockEnvironment());
        JpaTelegramPublicationSettingsRepository repository = new JpaTelegramPublicationSettingsRepository(entityManager, cipher);
        OffsetDateTime now = OffsetDateTime.parse("2026-06-24T10:00:00Z");
        TelegramPublicationSettingsEntity entity = new TelegramPublicationSettingsEntity(
                (short) 1,
                true,
                "https://api.telegram.org",
                cipher.encrypt("telegram-token"),
                "chat-id",
                true,
                now,
                now
        );
        when(entityManager.find(TelegramPublicationSettingsEntity.class, (short) 1)).thenReturn(entity);

        TelegramPublicationSettings settings = repository.find().orElseThrow();

        assertEquals("telegram-token", settings.getBotToken());
    }
}
