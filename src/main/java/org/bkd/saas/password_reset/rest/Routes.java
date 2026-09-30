package org.bkd.saas.password_reset.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.bkd.saas.shared.Routes.PUBLIC_BASE_PATH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

    public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/password";
    public static final String FORGOT_PASSWORD = PUBLIC_ENDPOINT + "/forgot";
    public static final String RESET_PASSWORD = PUBLIC_ENDPOINT + "/reset";
}
