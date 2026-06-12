package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserAccount execute(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
