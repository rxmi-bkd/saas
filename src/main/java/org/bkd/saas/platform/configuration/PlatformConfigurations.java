package org.bkd.saas.platform.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "social-authentication")
public class PlatformConfigurations {

  private PlatformConfiguration tiktok;
}
