package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static String normalizeEmail(@NonNull String email) {
    return email.trim().toLowerCase();
  }
}
