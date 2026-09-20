package org.bkd.saas.social_authentication.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class PlatformConfiguration {
    private String clientId;
    private String clientSecret;
    private String scope;
    private String redirectUri;
    private String responseType;
    private String grantType;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
}