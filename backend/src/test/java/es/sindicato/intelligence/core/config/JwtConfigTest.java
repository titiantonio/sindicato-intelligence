package es.sindicato.intelligence.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

    @Test
    void rejectsDevelopmentSecretInProductionProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        JwtConfig config = new JwtConfig(
                new JwtSecurityProperties(JwtConfig.DEFAULT_DEVELOPMENT_SECRET, "sindicato-intelligence", 15, 7),
                environment
        );

        assertThrows(IllegalStateException.class, config::validateSecret);
    }

    @Test
    void allowsDevelopmentSecretOutsideProductionProfile() {
        JwtConfig config = new JwtConfig(
                new JwtSecurityProperties(JwtConfig.DEFAULT_DEVELOPMENT_SECRET, "sindicato-intelligence", 15, 7),
                new MockEnvironment()
        );

        assertDoesNotThrow(config::validateSecret);
    }
}
