package org.bkd.saas.social_authentication.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.bkd.saas.shared.Routes.PUBLIC_BASE_PATH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

    public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/social-authentication";
    public static final String AUTHORIZE = PUBLIC_ENDPOINT + "/authorize/{platform}";
    public static final String HANDLE_CALLBACK = PUBLIC_ENDPOINT + "/callback/{platform}";
}
