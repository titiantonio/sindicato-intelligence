package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.core.config.JwtSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtTokenServiceTest {

    @Test
    void generatesAccessAndRefreshTokensWithExpectedClaims() {
        String secret = "super-secret-key-for-sindicato-jwt-test-123";
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")).build();
        jwtDecoder.setJwtValidator(token -> OAuth2TokenValidatorResult.success());
        Clock clock = Clock.fixed(Instant.parse("2026-06-10T10:00:00Z"), ZoneOffset.UTC);
        JwtTokenService service = new JwtTokenService(
                jwtEncoder,
                new JwtSecurityProperties(secret, "sindicato-intelligence", 15, 7),
                clock
        );
        AuthenticatedUser user = new AuthenticatedUser(1L, "admin@sindicato.es", "Admin Sindicato", "ADMIN", true);

        String accessToken = service.generateAccessToken(user);
        String refreshToken = service.generateRefreshToken(user);
        Jwt accessJwt = jwtDecoder.decode(accessToken);
        Jwt refreshJwt = jwtDecoder.decode(refreshToken);

        assertEquals("admin@sindicato.es", accessJwt.getSubject());
        assertEquals("ACCESS", accessJwt.getClaimAsString("tokenType"));
        assertEquals("ADMIN", accessJwt.getClaimAsString("role"));
        assertEquals(List.of("ADMIN"), accessJwt.getClaimAsStringList("roles"));
        assertEquals(true, accessJwt.getClaimAsBoolean("mustChangePassword"));
        assertEquals(Instant.parse("2026-06-10T10:15:00Z"), accessJwt.getExpiresAt());

        assertEquals("admin@sindicato.es", refreshJwt.getSubject());
        assertEquals("REFRESH", refreshJwt.getClaimAsString("tokenType"));
        assertNull(refreshJwt.getClaimAsStringList("roles"));
        assertNull(refreshJwt.getClaimAsString("role"));
        assertEquals(Instant.parse("2026-06-17T10:00:00Z"), refreshJwt.getExpiresAt());
    }
}
