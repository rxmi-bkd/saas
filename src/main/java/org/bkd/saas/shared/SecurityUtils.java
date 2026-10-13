package org.bkd.saas.shared;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  public static String hash(@NonNull String string) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    }
    catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
