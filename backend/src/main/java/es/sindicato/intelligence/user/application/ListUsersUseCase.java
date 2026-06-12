package es.sindicato.intelligence.user.application;

import es.sindicato.intelligence.user.domain.UserAccount;
import es.sindicato.intelligence.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> execute() {
        return userRepository.findAll();
    }
}
