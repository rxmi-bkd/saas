package org.bkd.saas.oidc.configuration;

public record PlatformConfiguration(
    String clientId,
    String clientSecret,
    String scope,
    String redirectUri,
    String responseType,
    String grantType,
    String authorizationUri,
    String tokenUri,
    String userInfoUri) {}
