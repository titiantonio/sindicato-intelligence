package es.sindicato.intelligence.auth.api;

import es.sindicato.intelligence.auth.application.LoginCommand;
import es.sindicato.intelligence.auth.application.LoginResult;
import es.sindicato.intelligence.auth.application.LoginUseCase;
import es.sindicato.intelligence.auth.application.RequestPasswordResetUseCase;
import es.sindicato.intelligence.auth.application.ResetPasswordUseCase;
import es.sindicato.intelligence.auth.application.ChangePasswordUseCase;
import es.sindicato.intelligence.user.application.ResetTemporaryPasswordUseCase;
import es.sindicato.intelligence.user.application.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
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
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase;

    public AuthController(
            LoginUseCase loginUseCase,
            RequestPasswordResetUseCase requestPasswordResetUseCase,
            ResetPasswordUseCase resetPasswordUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.resetTemporaryPasswordUseCase = resetTemporaryPasswordUseCase;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(loginUseCase.execute(new LoginCommand(request.email(), request.password())));
    }

    @PostMapping("/forgot-password")
    public MessageResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        requestPasswordResetUseCase.execute(request.email());
        return new MessageResponse("Si el email existe, se ha enviado un enlace de recuperacion.");
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(request.token(), request.newPassword());
        return new MessageResponse("Password actualizada correctamente.");
    }

    @PostMapping("/request-temporary-password")
    public MessageResponse requestTemporaryPassword(@Valid @RequestBody RequestTemporaryPasswordRequest request) {
        resetTemporaryPasswordUseCase.executeForEmailIfEligible(request.email());
        return new MessageResponse("Si el email existe y la password temporal ha expirado, se ha enviado una nueva password temporal.");
    }

    @PostMapping("/change-password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        changePasswordUseCase.execute(authentication.getName(), request.currentPassword(), request.newPassword());
        return new MessageResponse("Password cambiada correctamente.");
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

    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleExpiredCredentials(CredentialsExpiredException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(UserNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    private LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                new LoginResponse.UserResponse(
                        result.userId(),
                        result.userName(),
                    result.userRole(),
                    result.mustChangePassword()
                )
        );
    }
}
