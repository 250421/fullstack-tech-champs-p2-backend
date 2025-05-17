package com.revature.nflfantasydraft.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Marks this class as a configuration class for Spring
public class SecurityConfig {
    
    @Bean // Defines a bean for the SecurityFilterChain
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf
            // Disables CSRF protection for specific endpoints
            .ignoringRequestMatchers(
                "/api/users/register", 
                "/api/users/login", 
                "/api/players/**",
                "/api/teams/**", 
                "/api/users/bot/**"
            )
        )
        .authorizeHttpRequests(auth -> auth
            // Allows DELETE requests to "/api/users/bot/**" without authentication
            .requestMatchers(HttpMethod.DELETE, "/api/users/bot/**").permitAll()
            // Allows GET requests to "/api/teams/league/**" without authentication
            .requestMatchers(HttpMethod.GET, "/api/teams/league/**").permitAll()
            // Allows GET requests to "/api/players/not-drafted" without authentication
            .requestMatchers(HttpMethod.GET, "/api/players/not-drafted").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/teams/leaderboard").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/teams/team1/**").permitAll()
            // Allows public access to the specified endpoints
            .requestMatchers(
                "/api/players/**", 
                "/api/users/register",
                "/api/users/login", 
                "/api/users/me", 
                "/api/teams/**", 
                "/api/players/position/**",
                "/api/users/bot/**"
            ).permitAll()
            // Requires authentication for all other endpoints
            .anyRequest().authenticated()
        );
        // Builds and returns the SecurityFilterChain
        return http.build();
    }
}