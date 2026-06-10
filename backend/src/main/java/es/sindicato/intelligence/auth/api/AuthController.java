package es.sindicato.intelligence.auth.api;

import es.sindicato.intelligence.auth.application.LoginCommand;
import es.sindicato.intelligence.auth.application.LoginResult;
import es.sindicato.intelligence.auth.application.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(loginUseCase.execute(new LoginCommand(request.email(), request.password())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(BadCredentialsException exception) {
        return Map.of("error", "invalid credentials");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    private LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                new LoginResponse.UserResponse(
                        result.userId(),
                        result.userName(),
                        result.userRole()
                )
        );
    }
}
