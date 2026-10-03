package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

  public static ObjectMapper MAPPER = new ObjectMapper();
}
