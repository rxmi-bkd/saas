package org.bkd.saas.authentication.mapper;

import org.bkd.saas.authentication.db.RefreshTokenEntity;
import org.bkd.saas.authentication.dto.RefreshTokenDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

  RefreshTokenDto toRefreshTokenDto(RefreshTokenEntity refreshTokenEntity);
}
