package org.bkd.saas.authentication.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.dto.TokenPairDto;
import org.bkd.saas.authentication.exception.InvalidCredentialsException;
import org.bkd.saas.user.dto.UserWithPasswordDto;
import org.bkd.saas.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;

  public TokenPairDto login(String email, String password) {
    Optional<UserWithPasswordDto> user = userService.readOptionalUserWithPassword(email);

    if (user.isEmpty()) {
      throw new InvalidCredentialsException();
    }

    boolean isPasswordCorrect = passwordEncoder.matches(password, user.get().password());

    if (!isPasswordCorrect || !user.get().enabled()) {
      throw new InvalidCredentialsException();
    }

    String accessToken = accessTokenService.createJwt(user.get().id(), user.get().role());
    String refreshToken = refreshTokenService.createToken(user.get().id());
    return new TokenPairDto(accessToken, refreshToken);
  }

  public TokenPairDto refresh(String token) {
    return refreshTokenService.refreshToken(token);
  }

  public void logout(String rawRefreshToken) {
    refreshTokenService.revokeToken(rawRefreshToken);
  }
}
