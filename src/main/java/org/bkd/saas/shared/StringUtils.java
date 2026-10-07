package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static String normalizeEmail(String email) {
    if (email == null) {
      return null;
    }

    return email.trim().toLowerCase();
  }
}
