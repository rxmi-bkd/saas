package org.bkd.saas.social_authentication.state;

import org.bkd.saas.social_authentication.state.db.StateEntity;
import org.bkd.saas.social_authentication.state.dto.StateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StateMapper {

    StateDto toStateDto(StateEntity stateEntity);
}
