package com.example.payment_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // thêm vào để có thể phân quyền = @PreAuthorized mà không cần phân quyền = endpoint
public class SecurityConfig {

    private final String[] USER_END_POINT = {
            "/api/v1/payment","/api/v1/payment/**",
            "/api/v1/payment/**", "/api/v1/payment/"
    };
    private final String[] ADMIN_END_POINT = {
            "/api/v1/order/get-orders","/api/v1/order/{id}"
    };

    @Autowired
    CustomJwtDecoder customJwtDecoder;

@Bean
public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(
            request ->
                    request
                            .requestMatchers(HttpMethod.POST, USER_END_POINT).hasRole("customer")
                            .requestMatchers(HttpMethod.PUT, USER_END_POINT).hasRole("customer")
                            .requestMatchers(HttpMethod.DELETE, USER_END_POINT).hasRole("customer")
                            .requestMatchers(HttpMethod.PUT, USER_END_POINT).hasRole("customer")
//                            .requestMatchers(HttpMethod.GET, ADMIN_END_POINT).hasRole("ADMIN")
                            .anyRequest().authenticated());

    httpSecurity.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwtConfigurer ->
                    jwtConfigurer.decoder(customJwtDecoder)
                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
    );

    httpSecurity.cors(withDefaults());
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    return httpSecurity.build();
}
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        });
        return converter;
    }
}

