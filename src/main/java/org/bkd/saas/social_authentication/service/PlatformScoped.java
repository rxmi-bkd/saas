package org.bkd.saas.social_authentication.service;

import org.bkd.saas.social_authentication.dto.PlatformEnum;

public interface PlatformScoped {

    boolean supports(PlatformEnum platform);
}
