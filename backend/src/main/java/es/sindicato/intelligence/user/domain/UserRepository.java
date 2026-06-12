package es.sindicato.intelligence.user.domain;

import java.util.Optional;
import java.util.List;

public interface UserRepository {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(Long id);

    List<UserAccount> findAll();

    boolean existsByEmail(String email);

    UserAccount save(UserAccount user);
}
