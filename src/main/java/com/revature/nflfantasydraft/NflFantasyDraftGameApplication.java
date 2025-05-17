package com.revature.nflfantasydraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.revature.nflfantasydraft.Filters.AuthFilter;

@SpringBootApplication
public class NflFantasyDraftGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(NflFantasyDraftGameApplication.class, args);
	}

	// -- Implement a cors Filter here --
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedHeader("http://http://3.20.227.225:3000");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		source.registerCorsConfiguration("/**", config);
		
		registrationBean.setFilter(new CorsFilter(source));
		registrationBean.setOrder(0);
		return registrationBean;
	}

	// -- Implement a route Filter here --
	@Bean
	public FilterRegistrationBean<AuthFilter> filterRegistrationBean() {
		FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
		AuthFilter authFilter = new AuthFilter();
		registrationBean.setFilter(authFilter);
		registrationBean.addUrlPatterns( 
			"/api/leagues/*", 
			"/api/teams/*", 
			"/api/bots/*",
			"/api/draft_picks/*",
			"/api/users/me"
		);
		return registrationBean;
	}

	
}
  
