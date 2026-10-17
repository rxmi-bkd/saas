package org.bkd.saas.security.configuration;

import static org.bkd.saas.security.rest.Routes.ENABLE_USER;
import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.security.rest.Routes.RESET_PASSWORD;
import static org.bkd.saas.security.rest.Routes.SEND_ACTIVATION_EMAIL;
import static org.bkd.saas.security.rest.Routes.SEND_FORGOTTEN_PASSWORD_EMAIL;
import static org.bkd.saas.user.rest.Routes.CREATE_USER;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.security.dto.RateLimitPolicyEnum;
import org.bkd.saas.security.exception.TooManyRequestsException;
import org.bkd.saas.security.service.RateLimitService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private static final Map<String, RateLimitPolicyEnum> POLICY_BY_PATH = Map.of(LOGIN,
                                                                                RateLimitPolicyEnum.LOGIN_IP,
                                                                                SEND_FORGOTTEN_PASSWORD_EMAIL,
                                                                                RateLimitPolicyEnum.EMAIL_IP,
                                                                                SEND_ACTIVATION_EMAIL,
                                                                                RateLimitPolicyEnum.EMAIL_IP,
                                                                                CREATE_USER,
                                                                                RateLimitPolicyEnum.SIGNUP_IP,
                                                                                RESET_PASSWORD,
                                                                                RateLimitPolicyEnum.TOKEN_IP,
                                                                                ENABLE_USER,
                                                                                RateLimitPolicyEnum.TOKEN_IP);

  private final RateLimitService rateLimitService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !POLICY_BY_PATH.containsKey(request.getServletPath());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {
      rateLimitService.consumeOrThrow(POLICY_BY_PATH.get(request.getServletPath()), request.getRemoteAddr());
    }
    catch (TooManyRequestsException e) {
      response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(e.getRetryAfterSeconds()));
      response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage());
      return;
    }
    filterChain.doFilter(request, response);
  }
}
