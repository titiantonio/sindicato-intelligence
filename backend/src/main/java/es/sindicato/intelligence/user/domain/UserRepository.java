package es.sindicato.intelligence.user.domain;

import java.util.Optional;

public interface UserRepository {

    Optional<UserAccount> findByEmail(String email);
}
