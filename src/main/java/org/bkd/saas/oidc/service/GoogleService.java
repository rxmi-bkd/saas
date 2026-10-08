package org.bkd.saas.oidc.service;

import org.bkd.saas.oidc.configuration.OidcProperties;
import org.bkd.saas.oidc.dto.ProviderEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class GoogleService extends AbstractOidcService {

  public GoogleService(RestClient restClient, OidcProperties properties) {
    super(ProviderEnum.google, restClient, properties);
  }
}
