package org.bkd.saas.security.configuration;

import static org.bkd.saas.security.rest.Routes.LOGOUT;
import static org.bkd.saas.shared.rest.Routes.PUBLIC_BASE_PATH;

import org.bkd.saas.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      AuthenticationManager authenticationManager,
      SessionAuthenticationStrategy sessionAuthenticationStrategy,
      SecurityContextRepository securityContextRepository,
      AuthenticationEntryPoint authenticationEntryPoint,
      AccessDeniedHandler accessDeniedHandler,
      LogoutSuccessHandler logoutSuccessHandler) {
    return http.csrf(CsrfConfigurer::disable)
        .logout(x -> x.logoutUrl(LOGOUT).logoutSuccessHandler(logoutSuccessHandler))
        .authenticationManager(authenticationManager)
        .sessionManagement(x -> x.sessionAuthenticationStrategy(sessionAuthenticationStrategy))
        .securityContext(x -> x.securityContextRepository(securityContextRepository))
        .exceptionHandling(
            x ->
                x.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            x ->
                x.requestMatchers(PUBLIC_BASE_PATH + "/**", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      UserService userService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new SessionFixationProtectionStrategy();
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, exception) ->
        response.sendError(
            HttpStatus.UNAUTHORIZED.value(), "Authentication required. Please log in.");
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, exception) -> {
      String message =
          exception instanceof CsrfException
              ? "Missing or invalid CSRF token."
              : "You don't have access to this resource.";
      response.sendError(HttpStatus.FORBIDDEN.value(), message);
    };
  }

  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    return new HttpStatusReturningLogoutSuccessHandler();
  }
}
