package org.bkd.saas.password_reset.rest;

import static org.bkd.saas.shared.rest.Routes.PUBLIC_BASE_PATH;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

  public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/password";
  public static final String FORGOT_PASSWORD = PUBLIC_ENDPOINT + "/forgot";
  public static final String RESET_PASSWORD = PUBLIC_ENDPOINT + "/reset";
}
