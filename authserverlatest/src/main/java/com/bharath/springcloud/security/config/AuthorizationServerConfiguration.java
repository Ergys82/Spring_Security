package com.bharath.springcloud.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfiguration {

    private static final String ROLES_CLAIM = "roles";

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${keyFile}")
    private String keyFile;

    @Value("${password}")
    private String password;

    @Value("${alias}")
    private String alias;

    @Value("${providerUrl}")
    private String providerUrl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.userDetailsService(userDetailsService)
                .formLogin(Customizer.withDefaults()).build();


         /*http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.userDetailsService(userDetailsService)
                .formLogin(Customizer.withDefaults()).build();*/
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        JWKSet jwkSet = buildJWKSet();
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }


    private JWKSet buildJWKSet() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream(keyFile);) {
            keyStore.load(fis, alias.toCharArray());
            return JWKSet.load(keyStore, new PasswordLookup() {
                @Override
                public char[] lookupPassword(String name) {
                    return password.toCharArray();
                }
            });
        }
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer(providerUrl).build();

    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient registeredClient = RegisteredClient.withId("couponservice")
                .clientId("couponclientapp")
                .clientSecret(passwordEncoder.encode("9999"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oidcdebugger.com/debug")
                .scope("read")
                .scope("write")
                .tokenSettings(tokenSettings())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(30l))
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if(context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                Authentication principal = context.getPrincipal();
                Set<String> authorities = principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                context.getClaims().claim("roles", authorities);
            };
        };
    }
}



