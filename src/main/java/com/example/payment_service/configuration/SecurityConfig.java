package com.example.payment_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomJwtDecoder customJwtDecoder;

    // WEBHOOK FILTER CHAIN (PUBLIC - NO JWT AT ALL)
    @Bean
    @Order(1)
    public SecurityFilterChain webhookSecurity(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/api/v1/webhook/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                .oauth2ResourceServer(oauth2 -> oauth2.disable())
                .securityContext(security -> security.disable())
                .sessionManagement(session -> session.disable());

        log.info("Webhook SecurityFilterChain LOADED");

        return http.build();
    }

    // API FILTER CHAIN (JWT PROTECTED)
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/**").hasRole("customer")
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .bearerTokenResolver(loggingBearerTokenResolver())
                                .jwt(jwt ->
                                        jwt.decoder(customJwtDecoder)
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                );

        log.info("API SecurityFilterChain LOADED");

        return http.build();
    }

    // JWT ROLE MAPPING
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role != null && role.startsWith("ROLE_")) {
                role = role.substring(5);
            }
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }

    // BEARER TOKEN RESOLVER + FULL LOG
    @Bean
    public BearerTokenResolver loggingBearerTokenResolver() {

        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();

        return request -> {

            log.info("======= BearerTokenResolver =======");
            log.info("URI            : {}", request.getRequestURI());
            log.info("Method         : {}", request.getMethod());
            log.info("Authorization  : {}", request.getHeader("Authorization"));
            log.info("Stripe-Signature: {}", request.getHeader("Stripe-Signature"));
            log.info("User-Agent     : {}", request.getHeader("User-Agent"));
            log.info("Headers        : {}", Collections.list(request.getHeaderNames()));
            log.info("==================================");

            //ABSOLUTE BYPASS
            if (request.getRequestURI().startsWith("/api/v1/webhook/")) {
                log.info("WEBHOOK REQUEST - JWT BYPASSED");
                return null;
            }

            String token = resolver.resolve(request);
            log.info("Resolved token : {}", token);

            return token;
        };
    }
}
