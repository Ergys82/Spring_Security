package com.bharath.spring.security;

import jakarta.servlet.*;

import java.io.IOException;

public class MySecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("Before");
        chain.doFilter(request, response);
        System.out.println("After");
    }
}

// ServletRequest request is the incoming request from the client
// ServletResponse response is the response that will be sent back to the client
// FilterChain chain is used to pass the request and response to the next filter in the chain or to the target resource (like a servlet or JSP)
// chain.doFilter(request, response); passa la richiesta e la risposta al prossimo filtro nella catena, oppure al servlet finale se non ci sono altri filtri.
//   Serve per continuare il flusso di elaborazione della richiesta HTTP.
//   Se non la chiami, la richiesta si blocca e non raggiunge mai la risorsa destinata.

