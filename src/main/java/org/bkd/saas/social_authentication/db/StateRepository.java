package org.bkd.saas.social_authentication.db;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, UUID> {
  Optional<StateEntity> findByValue(String value);

  void deleteAllByExpiresAtBefore(Instant expiresAtBefore);
}
