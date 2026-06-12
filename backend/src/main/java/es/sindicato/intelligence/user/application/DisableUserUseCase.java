package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserStatus;
import org.springframework.stereotype.Service;

@Service
public class DisableUserUseCase {

    private final ChangeUserStatusUseCase changeUserStatusUseCase;

    public DisableUserUseCase(ChangeUserStatusUseCase changeUserStatusUseCase) {
        this.changeUserStatusUseCase = changeUserStatusUseCase;
    }

    public UserAccount execute(Long userId) {
        return execute(userId, "system");
    }

    public UserAccount execute(Long userId, String actorEmail) {
        return changeUserStatusUseCase.execute(userId, UserStatus.INACTIVE, actorEmail);
    }
}
