package org.bkd.saas.security.filter;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bkd.saas.authentication.service.AuthenticationTokenService;
import org.bkd.saas.user.db.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationTokenService authenticationTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractJwt(request);
        boolean isValidJwt = hasText(jwt) && authenticationTokenService.isValidJwt(jwt);

        if (isValidJwt) {
            UsernamePasswordAuthenticationToken auth = buildAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        boolean hasValidHeader = hasText(authorization) && authorization.startsWith(BEARER_PREFIX);
        if (hasValidHeader) return authorization.substring(BEARER_PREFIX.length());
        else return null;
    }

    private UsernamePasswordAuthenticationToken buildAuthenticationToken(String jwt) {
        UUID subject = authenticationTokenService.readSubject(jwt);
        Role role = authenticationTokenService.readRole(jwt);
        return new UsernamePasswordAuthenticationToken(subject, null, role.getAuthorities());
    }
}
