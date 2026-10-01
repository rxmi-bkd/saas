package org.bkd.saas.authentication.db;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  void deleteAllByExpiresAtBefore(Instant expiresAtBefore);

  Optional<RefreshTokenEntity> findByHash(String hash);

  List<RefreshTokenEntity> findByFamilyId(UUID familyId);
}
