package com.bharath.springcloud.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class WebSecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    // Before SpringBoot 6+
   /*@Bean
    AuthenticationManager authManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /*@Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin@example.com")
                        .passwordEncoder(password -> passwordEncoder().encode(password))
                        .password("adminpass")
                        .roles("ADMIN")
                        .build(),
                User.withUsername("user@example.com")
                        .passwordEncoder(password -> passwordEncoder().encode(password))
                        .password("userpass")
                        .roles("USER")
                        .build()
        );
    }*/

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {

        http.httpBasic(Customizer.withDefaults())
               .userDetailsService(userDetailsService);

        //http.formLogin(Customizer.withDefaults())
         //.userDetailsService(userDetailsService);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/couponapi/coupons/{code:^[A-Z]*$}", "/showGetCoupon", "/getCoupon")
                .hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/showCreateCoupon", "/createCoupon", "/createResponse")
                .hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/couponapi/coupons","/saveCoupon")
                .hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/getCoupon")
                .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/", "/login", "/showReg", "/registerUser").permitAll()

        );

        http.logout(logout -> logout.logoutSuccessUrl("/"));

        http.securityContext(context -> context.requireExplicitSave(true));

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
