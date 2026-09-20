package org.bkd.saas.social_authentication.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.social-authentication")
public class PlatformConfigurations {
    private PlatformConfiguration google;
}
