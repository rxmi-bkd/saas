package org.bkd.saas.authentication.db;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenEntity {

  @Id
  @Column(nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, updatable = false)
  private UUID userId;

  @Column(nullable = false, updatable = false, unique = true, length = 64)
  private String hash;

  @Column(nullable = false, updatable = false)
  private UUID familyId;

  @Column(nullable = false, updatable = false)
  private Instant expiresAt;

  @Column(nullable = true)
  private Instant revokedAt;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public RefreshTokenEntity(UUID userId, String hash, UUID familyId, Instant expiresAt) {
    this.userId = userId;
    this.hash = hash;
    this.familyId = familyId;
    this.expiresAt = expiresAt;
  }

  @PrePersist
  public void onCreate() {
    Instant now = Instant.now();

    if (createdAt == null) {
      createdAt = now;
    }
  }
}
