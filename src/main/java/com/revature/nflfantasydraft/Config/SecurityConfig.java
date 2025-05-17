// package com.revature.nflfantasydraft.Config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;



// @Configuration
// public class SecurityConfig {
    
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//         .csrf(csrf -> csrf
//         .ignoringRequestMatchers("/api/users/register", "/api/users/login", "/api/players/**",
//         "/api/teams/**", "/api/users/bot/**")
//     )
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/players/**", "/api/users/register",
//                  "/api/users/login", "/api/users/me", "/api/teams/**", "/api/players/position/**",
//            "/api/users/bot/**").permitAll()  // Allow public access
                
//                  .anyRequest().authenticated()  // Secure other endpoints
//             );
//         return http.build();
//     }
// }   