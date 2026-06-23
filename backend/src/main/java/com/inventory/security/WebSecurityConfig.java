package com.inventory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // enables @PreAuthorize on controller methods if needed later
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                             AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public — no token needed
//                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        // Role-specific API paths
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/supplier/**").hasAnyRole("SUPPLIER", "ADMIN")
                        .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "ADMIN")
                        // All /api/** endpoints require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors(cors -> {});

        return httpSecurity.build();
    }
}
