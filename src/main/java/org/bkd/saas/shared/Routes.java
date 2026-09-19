package org.bkd.saas.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Routes {

    public static final String BASE_PATH = "/api/v1";
    public static final String PUBLIC_BASE_PATH = BASE_PATH + "/public";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Authentication {

        public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/authentication";
        public static final String LOGIN = PUBLIC_ENDPOINT + "/login";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PasswordReset {

        public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/password";
        public static final String FORGOT_PASSWORD = PUBLIC_ENDPOINT + "/forgot";
        public static final String RESET_PASSWORD = PUBLIC_ENDPOINT + "/reset";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class User {

        public static final String PRIVATE_ENDPOINT = BASE_PATH + "/users";
        public static final String PUBLIC_ENDPOINT = PUBLIC_BASE_PATH + "/users";
        public static final String ME = PRIVATE_ENDPOINT + "/me";
        public static final String CREATE_USER = PUBLIC_ENDPOINT;
        public static final String UPDATE_USER_PASSWORD = PRIVATE_ENDPOINT + "/password";
        public static final String UPDATE_USER_EMAIL = PRIVATE_ENDPOINT + "/email";
    }
}
