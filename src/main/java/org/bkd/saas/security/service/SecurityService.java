package org.bkd.saas.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bkd.saas.notification.service.EmailService;
import org.bkd.saas.security.exception.InvalidCredentialsException;
import org.bkd.saas.security.exception.InvalidTokenException;
import org.bkd.saas.shared.StringUtils;
import org.bkd.saas.user.db.AppUserEntity;
import org.bkd.saas.user.dto.UserDto;
import org.bkd.saas.user.exception.DisabledUserException;
import org.bkd.saas.user.exception.UserAlreadyEnabledException;
import org.bkd.saas.user.exception.UserNotFoundException;
import org.bkd.saas.user.mapper.UserMapper;
import org.bkd.saas.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
  private final UserMapper userMapper;
  private final UserService userService;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;
  private final PasswordResetTokenService passwordResetTokenService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

  public UserDto login(String email, String password, HttpServletRequest request, HttpServletResponse response) {
    try {
      String normalized = StringUtils.normalizeEmail(email);
      UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(normalized,
                                                                                                      password);
      Authentication authenticated = authenticationManager.authenticate(token);
      AppUserEntity appUserEntity = (AppUserEntity) authenticated.getPrincipal();
      Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(appUserEntity.getId(),
                                                                                        null,
                                                                                        authenticated.getAuthorities());
      sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
      saveSecurityContext(authentication, request, response);
      publishInteractiveAuthenticationSuccessEvent(authentication);
      return userMapper.toUserDto(appUserEntity);
    }
    catch (DisabledException | LockedException e) {
      throw new DisabledUserException(email);
    }
    catch (BadCredentialsException e) {
      throw new InvalidCredentialsException();
    }
  }

  private void publishInteractiveAuthenticationSuccessEvent(Authentication authentication) {
    InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authentication,
                                                                                            SecurityService.class);
    applicationEventPublisher.publishEvent(event);
  }

  private void saveSecurityContext(Authentication authentication,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
    securityContextRepository.saveContext(securityContext, request, response);
  }

  public void sendActivationEmail(String email) {
    UserDto user = userService.readOptionalUser(email).orElseThrow(() -> new UserNotFoundException(email));

    if (user.enabled()) {
      throw new UserAlreadyEnabledException(email);
    }

    emailService.sendActivationEmail(user.id(), user.email());
  }

  public void forgotPassword(String email) {
    UserDto user = userService.readOptionalUser(email).orElseThrow(() -> new UserNotFoundException(email));

    String jwt = passwordResetTokenService.createJwt(user.id(), user.passwordVersion());

    emailService.sendResetPasswordEmail(user.email(), jwt);
  }

  public void resetPassword(String jwt, String newPassword) {
    boolean isValidJwt = passwordResetTokenService.isValidJwt(jwt);

    if (isValidJwt) {
      UUID userId = passwordResetTokenService.readSubject(jwt);
      userService.updateUserPassword(userId, newPassword);
      return;
    }

    throw new InvalidTokenException(jwt);
  }
}
