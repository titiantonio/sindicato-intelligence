package es.sindicato.intelligence.user.infrastructure;

import es.sindicato.intelligence.user.domain.UserPasswordHistoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class JpaUserPasswordHistoryRepository implements UserPasswordHistoryRepository {

    private final EntityManager entityManager;

    public JpaUserPasswordHistoryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> findHashesByUserId(Long userId) {
        return entityManager.createQuery(
                        "SELECT history.passwordHash FROM UserPasswordHistoryEntity history WHERE history.userId = :userId ORDER BY history.createdAt DESC",
                        String.class
                )
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public void save(Long userId, String passwordHash) {
        entityManager.persist(new UserPasswordHistoryEntity(userId, passwordHash, OffsetDateTime.now()));
    }
}
