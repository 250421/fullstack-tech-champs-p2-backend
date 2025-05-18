// package com.revature.nflfantasydraft;

// import org.junit.jupiter.api.Test;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.DefaultSecurityFilterChain;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AnyRequestMatcher;

// import com.revature.nflfantasydraft.Config.SecurityConfig;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// class SecurityConfigTest {

//     @Test
//     void filterChain_ShouldConfigureSecurity() throws Exception {
//         // Create mocks
//         HttpSecurity httpSecurity = mock(HttpSecurity.class);
        
//         // Create a real DefaultSecurityFilterChain instead of mocking the interface
//         DefaultSecurityFilterChain filterChain = new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE);
        
//         // Setup the method chain with RETURNS_SELF to handle the fluent API
//         when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
//         when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
//         when(httpSecurity.build()).thenReturn(filterChain);
        
//         // Create instance of SecurityConfig
//         SecurityConfig securityConfig = new SecurityConfig();
        
//         // Test
//         SecurityFilterChain result = securityConfig.filterChain(httpSecurity);
        
//         // Verify
//         assertNotNull(result, "SecurityFilterChain should not be null");
        
//         // Verify interactions
//         verify(httpSecurity).csrf(any());
//         verify(httpSecurity).authorizeHttpRequests(any());
//         verify(httpSecurity).build();
//     }
// }