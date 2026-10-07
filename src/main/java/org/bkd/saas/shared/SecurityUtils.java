package org.bkd.saas.shared;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {
  public static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static String hash(String string) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    } catch (NullPointerException e) {
      return null;
    }
  }

  public static String encodeToBase64(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
