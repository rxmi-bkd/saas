package org.bkd.saas.security.filter;

import static org.bkd.saas.security.service.AccessTokenService.ROLE_CLAIM;
import static org.springframework.util.StringUtils.hasText;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.service.AccessTokenService;
import org.bkd.saas.user.dto.RoleEnum;
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
    String jwt = extractJwt(request);
    boolean isValidJwt = hasText(jwt) && accessTokenService.isValidJwt(jwt);

    if (isValidJwt) {
      UsernamePasswordAuthenticationToken auth = buildAuthenticationToken(jwt);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwt(HttpServletRequest request) {
    String authorization = request.getHeader(AUTHORIZATION_HEADER);
    boolean hasValidHeader = hasText(authorization) && authorization.startsWith(BEARER_PREFIX);

    if (hasValidHeader) {
      return authorization.substring(BEARER_PREFIX.length());
    }

    return null;
  }

  private UsernamePasswordAuthenticationToken buildAuthenticationToken(String jwt) {
    Claims claims = accessTokenService.readJwt(jwt);
    UUID subject = UUID.fromString(claims.getSubject());
    RoleEnum role = claims.get(ROLE_CLAIM, RoleEnum.class);
    return new UsernamePasswordAuthenticationToken(subject, null, role.getAuthorities());
  }
}
