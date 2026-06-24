package es.sindicato.intelligence.core.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(JwtSecurityProperties.class)
public class JwtConfig {

    static final String DEFAULT_DEVELOPMENT_SECRET = "change-this-jwt-secret-in-production-min-32-bytes";

    private final JwtSecurityProperties jwtSecurityProperties;
    private final Environment environment;

    public JwtConfig(JwtSecurityProperties jwtSecurityProperties, Environment environment) {
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.environment = environment;
    }

    @PostConstruct
    void validateSecret() {
        if (jwtSecurityProperties.secret() == null || jwtSecurityProperties.secret().isBlank()) {
            throw new IllegalStateException("JWT secret is required");
        }

        if (jwtSecurityProperties.secret().getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must have at least 32 bytes");
        }

        if (isProductionProfile() && DEFAULT_DEVELOPMENT_SECRET.equals(jwtSecurityProperties.secret())) {
            throw new IllegalStateException("JWT secret must be provided explicitly in production");
        }
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    private SecretKey secretKey() {
        return new SecretKeySpec(jwtSecurityProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch("prod"::equalsIgnoreCase);
    }
}
