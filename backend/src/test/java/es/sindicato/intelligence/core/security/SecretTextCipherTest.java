package es.sindicato.intelligence.core.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecretTextCipherTest {

    @Test
    void encryptsAndDecryptsSecretText() {
        SecretTextCipher cipher = new SecretTextCipher("local-settings-encryption-key-32-bytes-min", new MockEnvironment());

        String encrypted = cipher.encrypt("telegram-token");

        assertNotEquals("telegram-token", encrypted);
        assertTrue(encrypted.startsWith("enc:v1:"));
        assertEquals("telegram-token", cipher.decryptIfNeeded(encrypted));
    }

    @Test
    void rejectsDevelopmentKeyInProdProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");

        assertThrows(IllegalStateException.class, () ->
                new SecretTextCipher("change-this-settings-encryption-key-in-production-min-32-bytes", environment)
        );
    }
}
