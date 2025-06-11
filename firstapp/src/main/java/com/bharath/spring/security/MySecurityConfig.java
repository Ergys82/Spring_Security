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

	// Functionality:
	//Returns a new instance of BCryptPasswordEncoder
	//BCrypt is a strong password hashing algorithm designed to be slow and resistant to brute-force attacks
	//It's commonly used in Spring Security applications for secure password storage
	//Usage: This encoder is used in other parts of the security configuration, specifically in:

	//The userDetailsService bean
	// where it's used to encode user passwords

	//The encodedPassword bean
	// where it's used to create an encoded version of a test password "cruise"

	@Bean
	String encodedPassword(BCryptPasswordEncoder encoder) {

		return "{bcrypt}" + encoder.encode("cruise");
	}

	// The encodedPassword function in MySecurityConfig.java is a Spring Bean that:
	//Takes a BCryptPasswordEncoder as a parameter
	//Returns a string that combines:
	// 1-The prefix {bcrypt} (indicating BCrypt hashing)
	// 2-The encoded version of the password "cruise"

	//This function is part of Spring Security's password handling mechanism.
	// It's used to demonstrate how passwords are stored in the application - they're hashed using BCrypt for security.
	// The {bcrypt} prefix is a Spring Security convention to identify the hashing algorithm used.
	//The function is defined as a @Bean, making it available for dependency injection throughout the Spring application context.
	// It's used in conjunction with the UserDetailsService bean to create a secure user authentication system
	// where passwords are never stored in plain text.
	//This is a common practice in Spring Security applications to ensure that passwords are securely stored and verified during authentication.



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

	// Method:
	//@Bean
	//UserDetailsService userDetailsService(BCryptPasswordEncoder encoder)

	//Declares a Spring Bean of type UserDetailsService, which is responsible for retrieving user information for authentication.
	//It takes a BCryptPasswordEncoder as a parameter, which will be used to securely hash the password.
	//This bean is typically used in Spring Security to authenticate users.

	// Inside the method:
	// 1. InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
	//   a- Creates a simple in-memory user store, useful for demos, tests, or lightweight apps.
	//   b- No database or external storage—everything is stored in memory.

	// 2. UserDetails user = User.withUsername("tom")
	//      .password(encoder.encode("cruise"))
	//      .authorities("read")
	//      .build();
	// This builds a user object with:
	// Username: tom
	// Password: "cruise" (but encoded using BCrypt)
	// Authorities (roles/permissions): read
	// So this user can log in with tom/cruise (assuming the correct password encoding
	// is used during login) and has permission to perform read-level operations.

	// BCrypt Encoding:
	//encoder.encode("cruise") means the password is hashed using BCrypt,
	// which is a strong password-hashing algorithm resistant to brute-force attacks.
	//This is critical because Spring Security compares the encoded form of the password during login.
	
// Before Spring Boot 3.2
	//  @Bean
	//	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	//
	//		http.httpBasic();
	//		http.authorizeHttpRequests().anyRequest().authenticated();
	//		return http.build();
	//	}

// After Spring Boot 3.2
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.httpBasic(Customizer.withDefaults());
		http.authorizeHttpRequests(auth -> auth
	            .anyRequest().authenticated());
		return http.build();
	}

}

// SecurityFilterChain filterChain(HttpSecurity http)
// Questo è il metodo di configurazione della sicurezza HTTP.
// HttpSecurity è l'oggetto che ti permette di configurare:
// 1-l’autenticazione,
// 2-le autorizzazioni,
// 3-filtri,
//e altre impostazioni di sicurezza.

// http.httpBasic(Customizer.withDefaults());
//Abilita l’autenticazione HTTP Basic
// (cioè: nome utente e password via header Authorization base64).
//Customizer.withDefaults() applica le impostazioni predefinite.

// http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
// Configura le regole di autorizzazione per le richieste HTTP.
// anyRequest().authenticated() vuol dire: tutte le richieste devono essere autenticate.
// Nessuna richiesta è pubblica. Nessuna eccezione.

// return http.build();
// Costruisce e restituisce l’istanza finale della SecurityFilterChain.

// In sintesi
//Questo bean:
// 1-abilita l’autenticazione HTTP Basic,
// 2-richiede che ogni richiesta sia autenticata,
// 3-non fa uso di login form o JWT o altre strategie.
