package org.bkd.saas.authentication.db;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE) // Used to prevent race condition during refresh
  Optional<RefreshTokenEntity> findByHash(String hash);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      "UPDATE refresh_token rt SET rt.revokedAt = :revokedAt WHERE rt.familyId = :familyId AND rt.revokedAt IS NULL")
  int revokeAllByFamilyId(@Param("familyId") UUID familyId, @Param("revokedAt") Instant revokedAt);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      "UPDATE refresh_token rt SET rt.revokedAt = :revokedAt WHERE rt.userId = :userId AND rt.revokedAt IS NULL")
  int revokeAllByUserId(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

  @Modifying
  @Query("DELETE refresh_token rt WHERE rt.expiresAt < :expiresAt")
  int deleteExpiredTokens(@Param("expiresAt") Instant expiresAt);
}
