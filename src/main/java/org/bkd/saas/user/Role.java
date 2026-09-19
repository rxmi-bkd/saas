package org.bkd.saas.user;

import static java.util.Collections.singletonList;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public List<GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(this.name());
        return singletonList(authority);
    }
}
