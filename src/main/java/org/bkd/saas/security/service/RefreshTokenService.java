package org.bkd.saas.security.service;

import static java.time.Instant.now;
import static org.bkd.saas.shared.SecurityUtils.SECURE_RANDOM;
import static org.bkd.saas.shared.SecurityUtils.encodeToBase64;
import static org.bkd.saas.shared.SecurityUtils.hash;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.db.RefreshTokenEntity;
import org.bkd.saas.security.db.RefreshTokenRepository;
import org.bkd.saas.security.dto.RefreshTokenDto;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.security.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenMapper refreshTokenMapper;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${app.jwt.refresh-token.expiration}")
  private long expirationInSeconds;

  public String createToken(UUID userId) {
    return createToken(userId, UUID.randomUUID());
  }

  public String createToken(UUID userId, UUID familyId) {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    String token = encodeToBase64(randomBytes);
    String hash = hash(token);
    Instant expiresAt = now().plusSeconds(expirationInSeconds);
    RefreshTokenEntity refresh = new RefreshTokenEntity(userId, hash, familyId, expiresAt);
    refreshTokenRepository.save(refresh);
    return token;
  }

  public RefreshTokenDto readToken(String token) {
    return readOptionalToken(token).orElseThrow(() -> new InvalidTokenException(token));
  }

  public Optional<RefreshTokenDto> readOptionalToken(String token) {
    return refreshTokenRepository
        .findByHash(hash(token))
        .map(refreshTokenMapper::toRefreshTokenDto);
  }

  public int revokeTokenFamily(UUID familyId) {
    Instant now = now();
    return refreshTokenRepository.revokeAllByFamilyId(familyId, now);
  }

  public int revokeTokenFamily(String token) {
    RefreshTokenDto tokenDto = readToken(token);
    return revokeTokenFamily(tokenDto.familyId());
  }

  public int revokeUserTokens(UUID userId) {
    Instant now = now();
    return refreshTokenRepository.revokeAllByUserId(userId, now);
  }

  public void revokeToken(UUID tokenId) {
    RefreshTokenEntity refreshToken =
        refreshTokenRepository
            .findById(tokenId)
            .orElseThrow(() -> new InvalidTokenException(tokenId));

    if (refreshToken.getRevokedAt() == null) {
      refreshToken.setRevokedAt(now());
      refreshTokenRepository.save(refreshToken);
    }
  }

  @Scheduled(cron = "0 0 * * * *")
  public int deleteExpiredTokens() {
    Instant now = now();
    return refreshTokenRepository.deleteExpiredTokens(now);
  }

  public boolean isRevokedToken(RefreshTokenDto token) {
    return token.revokedAt() != null;
  }

  public boolean isExpiredToken(RefreshTokenDto token) {
    Instant now = now();
    return token.expiresAt().isBefore(now);
  }
}
