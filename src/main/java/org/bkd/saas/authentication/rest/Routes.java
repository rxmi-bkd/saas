package org.bkd.saas.authentication.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.bkd.saas.shared.Routes.PUBLIC_BASE_PATH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

    public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/authentication";
    public static final String LOGIN = PUBLIC_ENDPOINT + "/login";
}
