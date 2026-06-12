package es.sindicato.intelligence.user.domain;

import java.util.List;

public interface UserPasswordHistoryRepository {

    List<String> findHashesByUserId(Long userId);

    void save(Long userId, String passwordHash);
}
