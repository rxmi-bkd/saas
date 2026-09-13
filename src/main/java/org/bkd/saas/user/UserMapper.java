package org.bkd.saas.user;

import org.bkd.saas.user.requests.responses.UserResponse;
import org.bkd.saas.user.requests.responses.UserWithPasswordResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toUserResponse(AppUser appUser);

  UserWithPasswordResponse toUserWithPasswordResponse(AppUser appUser);
}
