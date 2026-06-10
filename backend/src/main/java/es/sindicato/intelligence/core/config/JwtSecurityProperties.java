package es.sindicato.intelligence.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtSecurityProperties(
        String secret,
        String issuer,
        long accessTokenMinutes,
        long refreshTokenDays
) {
}
