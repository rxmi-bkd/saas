package org.bkd.saas.social_authentication.mapper;

import org.bkd.saas.social_authentication.db.StateEntity;
import org.bkd.saas.social_authentication.dto.StateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapper {

  StateDto toStateDto(StateEntity stateEntity);
}
