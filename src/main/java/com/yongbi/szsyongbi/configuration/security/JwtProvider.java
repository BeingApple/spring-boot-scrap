package com.yongbi.szsyongbi.configuration.security;

import com.yongbi.szsyongbi.token.domain.TokenFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtProvider {
    private final TokenFactory tokenFactory;
    private final AppUserDetailsService appUserDetailsService;

    public JwtProvider(TokenFactory tokenFactory, AppUserDetailsService appUserDetailsService) {
        this.tokenFactory = tokenFactory;
        this.appUserDetailsService = appUserDetailsService;
    }

    public Authentication getAuthentication(final String token) {

        final var userId = extractUserId(token);
        final var user = appUserDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public boolean verify(String encodedJwt) throws RuntimeException {
        return tokenFactory.verify(encodedJwt);
    }

    private String extractUserId(String encodedJwt) {
        try {
            return tokenFactory.getUserId(encodedJwt);
        } catch (RuntimeException ex) {
            log.error("Error with extractUserName", ex);

            return "";
        }
    }
}
