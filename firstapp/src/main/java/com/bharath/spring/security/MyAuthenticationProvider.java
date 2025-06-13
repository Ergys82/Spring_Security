package com.bharath.spring.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        if ("tom".equals(userName) && "cruise".equals(password)) {
            return new UsernamePasswordAuthenticationToken(userName, password, Arrays.asList());
        }else {
            throw new BadCredentialsException("Invalid Username or Password");
        }

    }
    // AuthenticationProvider is used to provide custom authentication logic.
    // Questa classe implementa un provider di autenticazione personalizzato per Spring Security.
    // Il metodo authenticate riceve le credenziali dell'utente e verifica se username e password corrispondono a "tom" e "cruise".
    // Se le credenziali sono corrette, restituisce un oggetto UsernamePasswordAuthenticationToken che rappresenta l'utente autenticato.
    // Se sono errate, lancia un'eccezione BadCredentialsException che indica autenticazione fallita.
    // Serve per gestire la logica di autenticazione in modo personalizzato, invece di usare quella predefinita di Spring Security.




    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    // The supports method tells Spring Security which type of authentication this provider can handle.
    //In this case, it's saying:
    //"I can handle authentication requests where the authentication object
    // is a UsernamePasswordAuthenticationToken."


}
