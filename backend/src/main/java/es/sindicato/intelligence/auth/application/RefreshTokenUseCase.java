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
import java.util.Objects;

@Service
public class RefreshTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

    private final JwtDecoder jwtDecoder;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public RefreshTokenUseCase(
            JwtDecoder jwtDecoder,
            JwtTokenService jwtTokenService,
            UserRepository userRepository
    ) {
        this.jwtDecoder = jwtDecoder;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public LoginResult execute(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("refresh token is required");
        }

        Jwt jwt = decode(refreshToken);
        if (!TokenType.REFRESH.name().equals(jwt.getClaimAsString("tokenType"))) {
            log.warn("refresh token rejected because token type is invalid: subject={}", jwt.getSubject());
            throw new BadCredentialsException("invalid refresh token");
        }

        UserAccount userAccount = userRepository.findByEmail(Objects.requireNonNull(jwt.getSubject(), "subject is required"))
                .orElseThrow(() -> new BadCredentialsException("invalid refresh token"));
        if (!userAccount.canAuthenticate()) {
            log.warn("refresh token rejected because user cannot authenticate: userId={}, status={}", userAccount.getId(), userAccount.getStatus());
            throw new BadCredentialsException("invalid refresh token");
        }
        if (userAccount.isTemporaryPasswordExpired(OffsetDateTime.now())) {
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

        log.info("refresh token accepted: userId={}, role={}", user.id(), user.role());
        return new LoginResult(
                jwtTokenService.generateAccessToken(user),
                jwtTokenService.generateRefreshToken(user),
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
