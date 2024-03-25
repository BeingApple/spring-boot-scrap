package com.yongbi.szsyongbi.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.security.domain.AppUserDetails;
import com.yongbi.szsyongbi.security.domain.Roles;
import com.yongbi.szsyongbi.token.domain.TokenFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;
    private final TokenFactory tokenFactory;

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/szs/login";
    private static final String HTTP_METHOD = "POST";


    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    public AuthenticationFilter(ObjectMapper objectMapper, TokenFactory tokenFactory) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getContentType().startsWith(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            final var ex = new AuthenticationServiceException(
                    "Unsupported request content type `" + request.getContentType() + "`");
            log.error("Authentication ContentType Error : "+request, ex);
            throw ex;
        }

        try {
            final var authentication = getAuthentication(request);
            return this.getAuthenticationManager().authenticate(authentication);

        } catch (IOException ex) {
            log.error("Authentication Attempt Error : "+request, ex);
            throw new AuthenticationServiceException("Wrong request content", ex);
        } catch (IllegalArgumentException ex) {
            log.error("Authentication Attempt Error : "+request, ex);
            throw new AuthenticationServiceException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        final var user = (AppUserDetails) authResult.getPrincipal();
        final var accessToken = tokenFactory.newUserAccessToken(user.getId(), user.getUsername(), user.getName());

        final var body = TokenResponseBody.success(accessToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("User Authentication Failure : " + request, failed);

        final var body = TokenResponseBody.failure(failed.getMessage());

        final int status;
        if (failed.getCause() instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST.value();
        } else {
            status = HttpStatus.UNAUTHORIZED.value();
        }

        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
            throws IOException, IllegalArgumentException {

        try (final var reader = request.getReader()) {
            final var json = reader.lines()
                    .collect(Collectors.joining());

            final var credential = objectMapper.readValue(json, UserCredential.class);
            credential.verify();

            return specificAuthentication(credential);
        }
    }

    private UsernamePasswordAuthenticationToken specificAuthentication(UserCredential credential) {

        return new UsernamePasswordAuthenticationToken(credential.getUserId(),
                credential.getPassword(),
                List.of(new SimpleGrantedAuthority(Roles.ROLE_USER.name())));
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCredential {
        public void verify () {
            if (!StringUtils.hasText(this.userId)) {
                throw new IllegalArgumentException("아이디는 필수값입니다.");
            }

            if (!StringUtils.hasText(this.password)) {
                throw new IllegalArgumentException("비밀번호는 필수값입니다.");
            }
        }

        private String userId;
        private String password;
    }
}
