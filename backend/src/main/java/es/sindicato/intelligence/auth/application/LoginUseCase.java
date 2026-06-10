package es.sindicato.intelligence.auth.application;

import es.sindicato.intelligence.auth.infrastructure.UserSecurityDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public LoginUseCase(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

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
        AuthenticatedUser user = new AuthenticatedUser(principal.id(), principal.getUsername(), principal.fullName(), principal.role());
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);

        log.info("login completed: userId={}, role={}", user.id(), user.role());

        return new LoginResult(
                accessToken,
                refreshToken,
                user.id(),
                user.name(),
                user.role()
        );
    }
}
