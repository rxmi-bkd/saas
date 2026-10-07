package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }
}
