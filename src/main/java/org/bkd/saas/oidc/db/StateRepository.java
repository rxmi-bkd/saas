package org.bkd.saas.oidc.db;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, UUID> {
  boolean existsByValue(String value);

  @Modifying
  @Query("delete from state s where s.value = :value and s.expiresAt > :now")
  int deleteUnexpiredByValue(@Param("value") String value, @Param("now") Instant now);

  void deleteAllByExpiresAtBefore(Instant expiresAtBefore);
}
