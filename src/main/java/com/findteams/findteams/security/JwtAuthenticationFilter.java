package com.findteams.findteams.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    // Skip JWT filter for preflight OPTIONS requests
    return request.getMethod().equalsIgnoreCase("OPTIONS");
}

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.info(token);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.warn("JWT token extraction failed: {}", e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(username, token)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")) // Customize roles as needed
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        logger.info("Request URI: {}", request.getRequestURI());
logger.info("Auth header: {}", authHeader);
logger.info("Extracted username: {}", username);
logger.info("Authentication set: {}", SecurityContextHolder.getContext().getAuthentication() != null);

        filterChain.doFilter(request, response);
    }
}