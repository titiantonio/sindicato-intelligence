package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
public class RefreshTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

    private final JwtDecoder jwtDecoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;
    private final UserRepository userRepository;

    public RefreshTokenUseCase(
            JwtDecoder jwtDecoder,
            JwtTokenService jwtTokenService,
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenHasher refreshTokenHasher,
            UserRepository userRepository
    ) {
        this.jwtDecoder = jwtDecoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenHasher = refreshTokenHasher;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoginResult execute(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("refresh token is required");
        }

        Jwt jwt = decode(refreshToken);
        if (!TokenType.REFRESH.name().equals(jwt.getClaimAsString("tokenType"))) {
            log.warn("refresh token rejected because token type is invalid: subject={}", jwt.getSubject());
            throw new BadCredentialsException("invalid refresh token");
        }
        String tokenId = jwt.getId();
        if (tokenId == null || tokenId.isBlank()) {
            log.warn("refresh token rejected because token id is missing: subject={}", jwt.getSubject());
            throw new BadCredentialsException("invalid refresh token");
        }

        OffsetDateTime now = OffsetDateTime.now();

        UserAccount userAccount = userRepository.findByEmail(Objects.requireNonNull(jwt.getSubject(), "subject is required"))
                .orElseThrow(() -> new BadCredentialsException("invalid refresh token"));
        RefreshTokenRecord tokenRecord = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new BadCredentialsException("invalid refresh token"));
        if (!Objects.equals(tokenRecord.userId(), userAccount.getId())
                || !tokenRecord.isActive(now)
                || !refreshTokenHasher.matches(refreshToken, tokenRecord.tokenHash())) {
            log.warn("refresh token rejected because persisted token state is invalid: userId={}", userAccount.getId());
            throw new BadCredentialsException("invalid refresh token");
        }
        if (!userAccount.canAuthenticate()) {
            log.warn("refresh token rejected because user cannot authenticate: userId={}, status={}", userAccount.getId(), userAccount.getStatus());
            throw new BadCredentialsException("invalid refresh token");
        }
        if (userAccount.isTemporaryPasswordExpired(now)) {
            log.warn("refresh token rejected because temporary password expired: userId={}", userAccount.getId());
            throw new CredentialsExpiredException("temporary password expired");
        }

        AuthenticatedUser user = new AuthenticatedUser(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getName(),
                userAccount.getRole().name(),
                userAccount.mustChangePassword()
        );

        refreshTokenRepository.markAsReplaced(tokenRecord.id(), now);
        GeneratedRefreshToken newRefreshToken = jwtTokenService.issueRefreshToken(user);
        refreshTokenRepository.create(
                user.id(),
                newRefreshToken.tokenId(),
                refreshTokenHasher.hash(newRefreshToken.value()),
                now,
                OffsetDateTime.ofInstant(newRefreshToken.expiresAt(), ZoneOffset.UTC)
        );

        log.info("refresh token accepted: userId={}, role={}", user.id(), user.role());
        return new LoginResult(
                jwtTokenService.generateAccessToken(user),
                newRefreshToken.value(),
                user.id(),
                user.name(),
                user.role(),
                user.mustChangePassword()
        );
    }

    private Jwt decode(String refreshToken) {
        try {
            return jwtDecoder.decode(refreshToken);
        } catch (JwtException exception) {
            log.warn("refresh token rejected because jwt decoding failed: reason={}", exception.getMessage());
            throw new BadCredentialsException("invalid refresh token", exception);
        }
    }
}
