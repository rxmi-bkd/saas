package org.bkd.saas.security.dto;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitPolicyEnum {
  LOGIN_IP(20, Duration.ofMinutes(15)), LOGIN_EMAIL(5, Duration.ofMinutes(15)), EMAIL_IP(10,
                                                                                         Duration.ofHours(1)), EMAIL_ADDRESS(3,
                                                                                                                             Duration.ofHours(1)), SIGNUP_IP(5,
                                                                                                                                                             Duration.ofHours(1)), TOKEN_IP(10,
                                                                                                                                                                                            Duration.ofMinutes(15));

  private final int capacity;
  private final Duration period;
}
