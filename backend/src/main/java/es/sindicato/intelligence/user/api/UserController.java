package es.sindicato.intelligence.user.api;

import es.sindicato.intelligence.user.application.ChangeUserStatusUseCase;
import es.sindicato.intelligence.user.application.CreateUserCommand;
import es.sindicato.intelligence.user.application.CreateUserUseCase;
import es.sindicato.intelligence.user.application.DeleteUserUseCase;
import es.sindicato.intelligence.user.application.DisableUserUseCase;
import es.sindicato.intelligence.user.application.GetUserUseCase;
import es.sindicato.intelligence.user.application.ListUsersUseCase;
import es.sindicato.intelligence.user.application.ResetTemporaryPasswordUseCase;
import es.sindicato.intelligence.user.application.UpdateUserCommand;
import es.sindicato.intelligence.user.application.UpdateUserUseCase;
import es.sindicato.intelligence.user.application.UserDeletionConflictException;
import es.sindicato.intelligence.user.application.UserNotFoundException;
import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DisableUserUseCase disableUserUseCase,
            ChangeUserStatusUseCase changeUserStatusUseCase,
            ResetTemporaryPasswordUseCase resetTemporaryPasswordUseCase,
            DeleteUserUseCase deleteUserUseCase,
            ListUsersUseCase listUsersUseCase,
            GetUserUseCase getUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
        this.resetTemporaryPasswordUseCase = resetTemporaryPasswordUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return listUsersUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return toResponse(getUserUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication
    ) {
        UserAccount created = createUserUseCase.execute(new CreateUserCommand(
                request.email(),
                request.name(),
                request.role()
        ), actor(authentication));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        UserAccount updated = updateUserUseCase.execute(id, new UpdateUserCommand(
                request.name(),
                request.role()
        ), actor(authentication));

        return toResponse(updated);
    }

    @PostMapping("/{id}/activate")
    public UserResponse activateUser(@PathVariable Long id, Authentication authentication) {
        return toResponse(changeUserStatusUseCase.execute(id, UserStatus.ACTIVE, actor(authentication)));
    }

    @PostMapping("/{id}/disable")
    public UserResponse disableUser(@PathVariable Long id, Authentication authentication) {
        return toResponse(disableUserUseCase.execute(id, actor(authentication)));
    }

    @PostMapping("/{id}/lock")
    public UserResponse lockUser(@PathVariable Long id, Authentication authentication) {
        return toResponse(changeUserStatusUseCase.execute(id, UserStatus.LOCKED, actor(authentication)));
    }

    @PostMapping("/{id}/unlock")
    public UserResponse unlockUser(@PathVariable Long id, Authentication authentication) {
        return toResponse(changeUserStatusUseCase.execute(id, UserStatus.ACTIVE, actor(authentication)));
    }

    @PostMapping("/{id}/reset-temporary-password")
    public UserResponse resetTemporaryPassword(@PathVariable Long id, Authentication authentication) {
        return toResponse(resetTemporaryPasswordUseCase.execute(id, actor(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        deleteUserUseCase.execute(id, actor(authentication));
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(UserNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(UserDeletionConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDeletionConflict(UserDeletionConflictException exception) {
        return Map.of("error", exception.getMessage());
    }

    private UserResponse toResponse(UserAccount userAccount) {
        return new UserResponse(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getName(),
                userAccount.getRole().name(),
                userAccount.isActive(),
                userAccount.mustChangePassword(),
                userAccount.getStatus().name(),
                userAccount.getTemporaryPasswordExpiresAt(),
                userAccount.getLastLoginAt(),
                userAccount.getLastPasswordChangeAt()
        );
    }

    private String actor(Authentication authentication) {
        return authentication == null ? "system" : authentication.getName();
    }
}
