package org.bkd.saas.oidc.service;

import org.bkd.saas.oidc.dto.PlatformEnum;

public interface PlatformScoped {

  boolean supports(PlatformEnum platform);
}
