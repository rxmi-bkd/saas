package org.bkd.saas.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static java.util.Collections.singletonList;

@Getter
@RequiredArgsConstructor
public enum Role {
  ROLE_USER, ROLE_ADMIN;

  public List<GrantedAuthority> getAuthorities() {
    GrantedAuthority authority = new SimpleGrantedAuthority(this.name());
    return singletonList(authority);
  }
}
