package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.auth.infrastructure.UserSecurityDetails;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class LoginUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final UserAuditLogRepository userAuditLogRepository;

    public LoginUseCase(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UserRepository userRepository,
            UserAuditLogRepository userAuditLogRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.userAuditLogRepository = userAuditLogRepository;
    }

    @Transactional
    public LoginResult execute(LoginCommand command) {
        Objects.requireNonNull(command, "command is required");

        log.info("login started: email={}", command.email());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.email(), command.password())
            );
        } catch (BadCredentialsException exception) {
            log.warn("login failed because credentials are invalid: email={}", command.email());
            throw exception;
        } catch (RuntimeException exception) {
            log.error("login failed: email={}, reason={}", command.email(), exception.getMessage(), exception);
            throw exception;
        }

        UserSecurityDetails principal = (UserSecurityDetails) authentication.getPrincipal();
        OffsetDateTime now = OffsetDateTime.now();
        if (principal.isTemporaryPasswordExpired(now)) {
            log.warn("login failed because temporary password expired: userId={}", principal.id());
            throw new CredentialsExpiredException("temporary password expired");
        }

        UserAccount storedUser = userRepository.findById(principal.id()).orElseThrow();
        UserAccount userWithLogin = userRepository.save(storedUser.withLastLoginAt(now));
        userAuditLogRepository.record(userWithLogin.getId(), userWithLogin.getEmail(), UserAuditAction.LOGIN, AuditDetailFormatter.login(now));

        AuthenticatedUser user = new AuthenticatedUser(
                userWithLogin.getId(),
                userWithLogin.getEmail(),
                userWithLogin.getName(),
                userWithLogin.getRole().name(),
                userWithLogin.mustChangePassword()
        );
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        log.info("login completed: userId={}, role={}", user.id(), user.role());

        return new LoginResult(
                accessToken,
                refreshToken,
                user.id(),
                user.name(),
                user.role(),
                user.mustChangePassword()
        );
    }
}
