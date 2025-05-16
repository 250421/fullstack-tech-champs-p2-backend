package com.revature.nflfantasydraft.Filters;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.revature.nflfantasydraft.Service.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filters are used to intercept incoming requests
public class AuthFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        System.out.println("=== AuthFilter Executed ===");
        System.out.println("Authorization Header: " + httpRequest.getHeader("Authorization"));

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null) {
            System.out.println("INSIDE AUTH HEADER FOUND");
            if (authHeader.startsWith("Bearer ")) {
                System.out.println("Token HAS FOUND 'Bearer ' prefix");
                // Extract the part after "Bearer "
                String token = authHeader.substring(7);
                try {
                    Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Constants.API_SECRET_KEY.getBytes()) // requires a byte array
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                    // Store the claims in request attributes
                    // This lets us access current userId from anywhere in the code
                    httpRequest.setAttribute("userId", Integer.parseInt(claims.get("userId").toString()));
                    System.out.println("Setting userId attribute: " + httpRequest.getAttribute("userId"));
                } catch (Exception e) {
                    System.out.println("Token is NOT VALID");
                    // Return an error with a message included. 
                    // (this is better than using sendError b/c sendError does not return with a message)
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpResponse.setContentType("application/json");
                    httpResponse.getWriter().write("{\"error\": \"invalid/expired token\"}");
                    return;
                }
            } else {
                System.out.println("Token is missing 'Bearer ' prefix");
                // Return an error with a message included. 
                // (this is better than using sendError b/c sendError does not return with a message)
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\": \"Authorization token must be Bearer [token]\"}");
                return;
            }
        } else {
            System.out.println("HEADER IS EMPTY");
            // Return an error with a message included. 
            // (this is better than using sendError b/c sendError does not return with a message)
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Authorization token must be provided\"}");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    
}  