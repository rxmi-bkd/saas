package org.bkd.saas.oidc.configuration;

import java.util.Map;
import org.bkd.saas.oidc.dto.PlatformEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.social-authentication")
public record OidcProperties(Map<PlatformEnum, PlatformConfiguration> providers) {}
