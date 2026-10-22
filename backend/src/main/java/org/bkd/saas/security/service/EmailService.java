package org.bkd.saas.security.service;

import org.bkd.saas.notification.dto.ChannelEnum;
import org.bkd.saas.notification.dto.NotificationDto;
import org.bkd.saas.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class EmailService {
  private static final String ENABLE_ACCOUNT_PATH = "/enable-account";
  private static final String TOKEN_QUERY_PARAM = "token";

  private final ActivationTokenService activationTokenService;
  private final NotificationService notificationService;
  private final String frontendBaseUrl;

  public EmailService(ActivationTokenService activationTokenService,
                      NotificationService notificationService,
                      @Value("${app.frontend.base-url}") String frontendBaseUrl) {
    this.activationTokenService = activationTokenService;
    this.notificationService = notificationService;
    this.frontendBaseUrl = frontendBaseUrl;
  }

  public void sendActivationEmail(UUID userId, String userEmail) {
    String jwt = activationTokenService.createJwt(userId);

    String url = UriComponentsBuilder.fromUriString(frontendBaseUrl)
                                               .path(ENABLE_ACCOUNT_PATH)
                                               .queryParam(TOKEN_QUERY_PARAM, jwt)
                                               .build()
                                               .toUriString();

    notificationService.sendNotification(ChannelEnum.EMAIL,
                                         new NotificationDto(userEmail,
                                                             "Activate your account",
                                                             "Click this link to activate your account: " + url));
  }

  public void sendForgottenPasswordEmail(String userEmail, String jwt) {
    notificationService.sendNotification(ChannelEnum.EMAIL,
                                         new NotificationDto(userEmail,
                                                             "Forgot your password ?",
                                                             "Use this token to reset your password: " + jwt));
  }
}
