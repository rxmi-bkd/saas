package org.bkd.saas.oidc.db;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity(name = "state")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false, unique = true)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String value;

  @Column(nullable = false)
  private Instant expiresAt;
}
