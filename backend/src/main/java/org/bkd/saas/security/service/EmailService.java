package org.bkd.saas.security.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.notification.dto.ChannelEnum;
import org.bkd.saas.notification.dto.NotificationDto;
import org.bkd.saas.notification.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final ActivationTokenService activationTokenService;
  private final NotificationService notificationService;

  public void sendActivationEmail(UUID userId, String userEmail) {
    String jwt = activationTokenService.createJwt(userId);
    notificationService.sendNotification(ChannelEnum.EMAIL,
                                         new NotificationDto(userEmail,
                                                             "Activate your account",
                                                             "Use this token to activate your account: " + jwt));
  }

  public void sendForgottenPasswordEmail(String userEmail, String jwt) {
    notificationService.sendNotification(ChannelEnum.EMAIL,
                                         new NotificationDto(userEmail,
                                                             "Forgot your password ?",
                                                             "Use this token to reset your password: " + jwt));
  }
}
