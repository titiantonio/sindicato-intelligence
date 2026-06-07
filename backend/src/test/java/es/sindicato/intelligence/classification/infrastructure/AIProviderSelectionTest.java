package es.sindicato.intelligence.classification.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.classification.application.AIProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class AIProviderSelectionTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void usesDeterministicProviderByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AIProvider.class);
            assertThat(context.getBean(AIProvider.class)).isInstanceOf(DeterministicAIProvider.class);
        });
    }

    @Test
    void usesGeminiProviderWhenConfigured() {
        contextRunner
                .withPropertyValues("app.ai.provider=gemini")
                .run(context -> {
                    assertThat(context).hasSingleBean(AIProvider.class);
                    assertThat(context.getBean(AIProvider.class)).isInstanceOf(GeminiAIProvider.class);
                });
    }

    @Configuration
    @Import({AiProviderProperties.class, DeterministicAIProvider.class, GeminiAIProvider.class})
    static class TestConfiguration {

        @Bean
        RestClient.Builder restClientBuilder() {
            return RestClient.builder();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
