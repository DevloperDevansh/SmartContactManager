package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	// Bean to provide the UserDetailsService implementation
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}
	
    // Bean for password encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Bean for authentication provider
    public DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider daoauthenticationProvider = new DaoAuthenticationProvider();
    	daoauthenticationProvider.setUserDetailsService(this.getUserDetailService());
    	
    	return daoauthenticationProvider;
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeRequests()
                .requestMatchers("/admin/**").hasRole("ADMIN") // Access restriction for /admin/**
                .requestMatchers("/user/**").hasRole("USER")   // Access restriction for /user/**
                .requestMatchers("/**").permitAll()            // Allow access to all other paths
                .and()
            .formLogin()                                     // Configuration for form-based login
            .loginPage("/signin")                            // Custom login page
                .and()
            .csrf().disable()                                // Disable CSRF protection
            .build();
    }
}
