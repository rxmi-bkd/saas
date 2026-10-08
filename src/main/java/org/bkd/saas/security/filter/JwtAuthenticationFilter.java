package org.bkd.saas.security.filter;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.AccessTokenClaimsDto;
import org.bkd.saas.security.service.AccessTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final AccessTokenService accessTokenService;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Optional<String> jwt = extractJwt(request);
    boolean isValidJwt = jwt.isPresent() && accessTokenService.isValidJwt(jwt.get());

    if (isValidJwt) {
      UsernamePasswordAuthenticationToken auth = buildAuthenticationToken(jwt.get());
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  private Optional<String> extractJwt(HttpServletRequest request) {
    String authorization = request.getHeader(AUTHORIZATION_HEADER);
    boolean hasValidHeader = hasText(authorization) && authorization.startsWith(BEARER_PREFIX);

    if (hasValidHeader) {
      return Optional.of(authorization.substring(BEARER_PREFIX.length()));
    }

    return Optional.empty();
  }

  private UsernamePasswordAuthenticationToken buildAuthenticationToken(String jwt) {
    AccessTokenClaimsDto accessTokenDto = accessTokenService.readJwt(jwt);
    return new UsernamePasswordAuthenticationToken(
        accessTokenDto.userId(), null, accessTokenDto.role().getAuthorities());
  }
}
