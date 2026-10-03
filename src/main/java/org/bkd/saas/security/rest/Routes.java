package org.bkd.saas.security.rest;

import static org.bkd.saas.shared.rest.Routes.PUBLIC_BASE_PATH;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

  public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/authentication";
  public static final String LOGIN = PUBLIC_ENDPOINT + "/login";
  public static final String REFRESH = PUBLIC_ENDPOINT + "/refresh";
  public static final String LOGOUT = PUBLIC_ENDPOINT + "/logout";
  public static final String FORGOT_PASSWORD = PUBLIC_ENDPOINT + "/password/forgot";
  public static final String RESET_PASSWORD = PUBLIC_ENDPOINT + "/password/reset";
}
