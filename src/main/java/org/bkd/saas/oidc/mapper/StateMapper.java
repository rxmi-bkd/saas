package org.bkd.saas.oidc.mapper;

import org.bkd.saas.oidc.db.StateEntity;
import org.bkd.saas.oidc.dto.StateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapper {

  StateDto toStateDto(StateEntity stateEntity);
}
