package org.bkd.saas.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.notification.dto.ChannelEnum;
import org.bkd.saas.notification.dto.NotificationDto;
import org.bkd.saas.notification.service.NotificationService;
import org.bkd.saas.security.service.ActivationTokenService;
import org.bkd.saas.shared.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserEmailService {

  private final ActivationTokenService activationTokenService;
  private final NotificationService notificationService;

  public void sendActivationEmail(UUID userId, String userEmail) {
    String email = StringUtils.normalizeEmail(userEmail);
    String jwt = activationTokenService.createJwt(userId);
    notificationService.sendNotification(ChannelEnum.EMAIL,
                                         new NotificationDto(email,
                                                             "Activate your account",
                                                             "Use this token to activate your account: " + jwt));
  }
}
