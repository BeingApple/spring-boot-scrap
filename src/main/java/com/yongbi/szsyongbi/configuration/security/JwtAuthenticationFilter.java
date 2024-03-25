package com.yongbi.szsyongbi.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var token = extractAuthorizationToken(request);
        if (token != null) {
            try {
                final var authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (RuntimeException ex) {
                unauthorizedResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String encodedJwt) {
        final var isValid = jwtProvider.verify(encodedJwt);
        if (!isValid) {
            throw new RuntimeException("JWT token is invalid");
        }

        return jwtProvider.getAuthentication(encodedJwt);
    }

    private void unauthorizedResponse(HttpServletResponse response) throws IOException {
        final var map = new HashMap<String, Object>();
        map.put("result", false);
        map.put("message", "Unauthorized");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }

    @Nullable
    private String extractAuthorizationToken(final HttpServletRequest request) {
        final var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }

        return authHeader.replaceFirst("(?i)^bearer ", "");
    }
}
