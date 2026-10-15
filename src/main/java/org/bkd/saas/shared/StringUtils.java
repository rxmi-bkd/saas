package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static final int PASSWORD_MIN_LENGTH = 12;
  public static final int PASSWORD_MAX_LENGTH = 24;

  public static String normalizeEmail(@NonNull String email) {
    return email.trim().toLowerCase();
  }
}
