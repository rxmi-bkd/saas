package org.bkd.saas.user.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.bkd.saas.shared.Routes.BASE_PATH;
import static org.bkd.saas.shared.Routes.PUBLIC_BASE_PATH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

    public static final String PRIVATE_ENDPOINT = BASE_PATH + "/users";
    public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/users";
    public static final String ME = PRIVATE_ENDPOINT + "/me";
    public static final String CREATE_USER = PUBLIC_ENDPOINT;
    public static final String UPDATE_USER_PASSWORD = PRIVATE_ENDPOINT + "/password";
    public static final String UPDATE_USER_EMAIL = PRIVATE_ENDPOINT + "/email";
}
