package org.bkd.saas.security.mapper;

import org.bkd.saas.security.db.RefreshTokenEntity;
import org.bkd.saas.security.dto.RefreshTokenDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

  RefreshTokenDto toRefreshTokenDto(RefreshTokenEntity refreshTokenEntity);
}
