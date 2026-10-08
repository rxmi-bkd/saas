package org.bkd.saas.oidc.rest;

import static org.bkd.saas.shared.rest.Routes.PUBLIC_BASE_PATH;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

  public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/social-authentication";
  public static final String AUTHORIZE = PUBLIC_ENDPOINT + "/authorize/{provider}";
  public static final String HANDLE_CALLBACK = PUBLIC_ENDPOINT + "/callback/{provider}";
}
