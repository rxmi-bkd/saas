package org.bkd.saas.authentication.service;

import static java.time.Instant.now;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.db.RefreshTokenEntity;
import org.bkd.saas.authentication.db.RefreshTokenRepository;
import org.bkd.saas.authentication.dto.TokenPairDto;
import org.bkd.saas.authentication.exception.InvalidRefreshTokenException;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
  private final UserService userService;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenRepository refreshTokenRepository;
  private static final SecureRandom secureRandom = new SecureRandom();

  @Value("${app.jwt.refresh-token.expiration}")
  private long expirationInSeconds;

  public String createToken(UUID userId) {
    return createToken(userId, UUID.randomUUID());
  }

  private String createToken(UUID userId, UUID familyId) {
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    String hash = hash(token);
    Instant expiresAt = now().plusSeconds(expirationInSeconds);
    RefreshTokenEntity refresh = new RefreshTokenEntity(userId, hash, familyId, expiresAt);
    refreshTokenRepository.save(refresh);
    return token;
  }

  // The family revocation on reuse must be committed even though we throw afterward.
  @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
  public TokenPairDto refreshToken(String token) {
    RefreshTokenEntity current =
        refreshTokenRepository
            .findByHash(hash(token))
            .orElseThrow(InvalidRefreshTokenException::new);

    if (isRevoked(current)) {
      // A rotated token is being replayed: assume theft and kill the whole family.
      revokeTokenFamily(current.getFamilyId());
      throw new InvalidRefreshTokenException();
    }

    if (isExpired(current)) {
      throw new InvalidRefreshTokenException();
    }

    Optional<UserDto> user = userService.readOptionalUser(current.getUserId());

    if (user.isEmpty()) {
      revokeToken(token);
      throw new InvalidRefreshTokenException();
    }

    if (!user.get().enabled()) {
      revokeToken(token);
      throw new InvalidRefreshTokenException();
    }

    current.setRevokedAt(now());
    refreshTokenRepository.save(current);

    String access = accessTokenService.createJwt(user.get().id(), user.get().role());
    String refresh = createToken(current.getUserId(), current.getFamilyId());
    return new TokenPairDto(access, refresh);
  }

  public void revokeTokenFamily(UUID familyId) {
    Instant now = now();
    List<RefreshTokenEntity> family = refreshTokenRepository.findByFamilyId(familyId);
    family.forEach(refreshTokenEntity -> refreshTokenEntity.setRevokedAt(now));
    refreshTokenRepository.saveAll(family);
  }

  public void revokeToken(String token) {
    refreshTokenRepository
        .findByHash(hash(token))
        .ifPresent(refreshToken -> revokeTokenFamily(refreshToken.getFamilyId()));
  }

  @Scheduled(cron = "0 0 * * * *")
  public void deleteExpiredTokens() {
    Instant now = now();
    refreshTokenRepository.deleteAllByExpiresAtBefore(now);
  }

  private String hash(String token) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  private boolean isRevoked(RefreshTokenEntity refreshTokenEntity) {
    return refreshTokenEntity.getRevokedAt() != null;
  }

  private boolean isExpired(RefreshTokenEntity refreshTokenEntity) {
    Instant now = now();
    return refreshTokenEntity.getExpiresAt().isBefore(now);
  }
}
