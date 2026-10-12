package org.bkd.saas.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpMessageConverterAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static org.bkd.saas.security.rest.Routes.LOGIN;
import static org.bkd.saas.shared.rest.Routes.PUBLIC_BASE_PATH;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage(LOGIN)
                        .successHandler(authenticationSuccessHandler())
                ).authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_BASE_PATH + "/**", "/error")
                .permitAll()
                .anyRequest()
                .authenticated())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new HttpMessageConverterAuthenticationSuccessHandler();
    }
}
