package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.core.config.JwtSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final JwtEncoder jwtEncoder;
    private final JwtSecurityProperties jwtSecurityProperties;
    private final Clock clock;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            JwtSecurityProperties jwtSecurityProperties,
            Clock clock
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtSecurityProperties = jwtSecurityProperties;
        this.clock = clock;
    }

    public String generateAccessToken(AuthenticatedUser user) {
        Objects.requireNonNull(user, "user is required");

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(jwtSecurityProperties.accessTokenMinutes(), ChronoUnit.MINUTES);
        String token = encode(user, TokenType.ACCESS, issuedAt, expiresAt);

        log.info(
                "jwt access token generated: userId={}, role={}, expiresInMinutes={}",
                user.id(),
                user.role(),
                jwtSecurityProperties.accessTokenMinutes()
        );
        return token;
    }

    public String generateRefreshToken(AuthenticatedUser user) {
        return issueRefreshToken(user).value();
    }

    public GeneratedRefreshToken issueRefreshToken(AuthenticatedUser user) {
        Objects.requireNonNull(user, "user is required");

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(jwtSecurityProperties.refreshTokenDays(), ChronoUnit.DAYS);
        String tokenId = UUID.randomUUID().toString();
        String token = encode(user, TokenType.REFRESH, issuedAt, expiresAt, tokenId);

        log.info(
                "jwt refresh token generated: userId={}, role={}, expiresInDays={}",
                user.id(),
                user.role(),
                jwtSecurityProperties.refreshTokenDays()
        );
        return new GeneratedRefreshToken(token, tokenId, expiresAt);
    }

    private String encode(AuthenticatedUser user, TokenType tokenType, Instant issuedAt, Instant expiresAt) {
        return encode(user, tokenType, issuedAt, expiresAt, null);
    }

    private String encode(AuthenticatedUser user, TokenType tokenType, Instant issuedAt, Instant expiresAt, String tokenId) {
        JwtClaimsSet.Builder claimsSetBuilder = JwtClaimsSet.builder()
                .issuer(jwtSecurityProperties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.email())
                .claim("userId", user.id())
                .claim("name", user.name())
                .claim("tokenType", tokenType.name())
                .claim("aud", List.of("sindicato-intelligence-api"))
                .claim("ctx", Map.of("module", "auth"));

        if (tokenId != null) {
            claimsSetBuilder.id(tokenId);
        }

        if (tokenType == TokenType.ACCESS) {
            claimsSetBuilder
                    .claim("role", user.role())
                    .claim("roles", List.of(user.role()))
                    .claim("mustChangePassword", user.mustChangePassword());
        }

        JwtClaimsSet claimsSet = claimsSetBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(() -> "HS256").build(), claimsSet)).getTokenValue();
    }
}
