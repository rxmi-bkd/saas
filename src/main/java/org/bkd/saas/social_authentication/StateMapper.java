package org.bkd.saas.social_authentication;

import org.bkd.saas.social_authentication.db.StateEntity;
import org.bkd.saas.social_authentication.dto.StateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StateMapper {

    StateDto toStateDto(StateEntity stateEntity);
}
