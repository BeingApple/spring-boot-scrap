package com.yongbi.szsyongbi.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongbi.szsyongbi.configuration.security.AuthenticationFilter;
import com.yongbi.szsyongbi.configuration.security.JwtAuthenticationFilter;
import com.yongbi.szsyongbi.configuration.security.JwtProvider;
import com.yongbi.szsyongbi.configuration.security.AppUserDetailsService;
import com.yongbi.szsyongbi.token.domain.TokenFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final TokenFactory tokenFactory;
    private final JwtProvider jwtProvider;
    private final AppUserDetailsService appUserDetailsService;

    public SecurityConfig(ObjectMapper objectMapper, TokenFactory tokenFactory, JwtProvider jwtProvider, AppUserDetailsService appUserDetailsService) {
        this.objectMapper = objectMapper;
        this.tokenFactory = tokenFactory;
        this.jwtProvider = jwtProvider;
        this.appUserDetailsService = appUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/favicon.ico", "/h2-console/**").permitAll()
                        .requestMatchers("/3o3/swagger.html", "/v3/**").permitAll()
                        .requestMatchers("/", "/szs/login").permitAll()
                )
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().permitAll());

        http
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(appUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    public AuthenticationManager authenticationManager() throws Exception {
        final var provider = daoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(provider);
    }

    private AuthenticationFilter authenticationFilter() {
        try {
            final var filter = new AuthenticationFilter(objectMapper, tokenFactory);
            filter.setAuthenticationManager(authenticationManager());
            return filter;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, objectMapper);
    }
}
