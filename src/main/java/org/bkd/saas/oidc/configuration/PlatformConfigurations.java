package org.bkd.saas.oidc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.social-authentication")
public record PlatformConfigurations(PlatformConfiguration google) {}
