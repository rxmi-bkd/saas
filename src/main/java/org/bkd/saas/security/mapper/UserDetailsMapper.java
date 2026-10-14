package org.bkd.saas.security.mapper;

import org.bkd.saas.security.dto.UserDetailsDto;
import org.bkd.saas.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailsMapper {

  UserDetailsDto toUserDetailsDto(UserDto user);
}
