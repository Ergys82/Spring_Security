package com.bharath.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MySecurityConfig {

	@Bean
	BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	String encodedPassword(BCryptPasswordEncoder encoder) {
		return "{bcrypt}" + encoder.encode("cruise");
	}

	@Bean
	UserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {
		InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
		UserDetails user = User.withUsername("tom")
				.password(encoder.encode("cruise"))
				.authorities("read")
				.build();
		userDetailsService.createUser(user);
		return userDetailsService;
	}
	

	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.httpBasic(Customizer.withDefaults());
		http.authorizeHttpRequests(auth -> auth
	            .anyRequest().authenticated());
		return http.build();
	}

}
