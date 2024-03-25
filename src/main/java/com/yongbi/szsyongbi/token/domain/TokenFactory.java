package com.yongbi.szsyongbi.token.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Slf4j
public class TokenFactory {
    public static final int DEFAULT_TOKEN_LIFETIME = 10;

    private final ObjectMapper objectMapper;

    private final SecretKey signer;
    private final String issuer;
    private final String subject;

    private final String ID_KEY = "id";
    private final String USER_ID_KEY = "userId";
    private final String NAME_KEY = "name";

    public TokenFactory(
            ObjectMapper mapper,
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.subject}") String subject) {
        this.objectMapper = mapper;
        this.signer = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.issuer = issuer;
        this.subject = subject;
    }

    public String newUserAccessToken(final Long id,
                                            final String userId,
                                            final String name,
                                            final int lifetime) {
        final var now = ZonedDateTime.now(ZoneOffset.UTC);
        final var expiration = now.plusMinutes(lifetime);


        final var jwt = Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now.toInstant()))
                .subject(subject)
                .expiration(Date.from(expiration.toInstant()))
                .claim(ID_KEY, id)
                .claim(USER_ID_KEY, userId)
                .claim(NAME_KEY, name);

        return jwt.signWith(signer).compact();
    }

    public String newUserAccessToken(Long id, String userId, String name) {
        return this.newUserAccessToken(id,
                userId,
                name,
                DEFAULT_TOKEN_LIFETIME);
    }

    public boolean verify(String encodedJwt) {
        try {
            Jwts.parser()
                    .verifyWith(signer)
                    .build()
                    .parse(encodedJwt);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.info("Invalid JWT Token", ex);
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT Token", ex);
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported Jwt Token", ex);
        } catch (IllegalArgumentException ex) {
            log.info("JWT Claims String is Empty", ex);
        }

        return false;
    }

    public Long getId(String encodedJwt) {
        return Jwts.parser()
                .json(new JacksonDeserializer<>(objectMapper))
                .verifyWith(signer)
                .build()
                .parseSignedClaims(encodedJwt)
                .getPayload()
                .get(ID_KEY, Long.class);
    }

    public String getUserId(String encodedJwt) {
        return Jwts.parser()
                .json(new JacksonDeserializer<>(objectMapper))
                .verifyWith(signer)
                .build()
                .parseSignedClaims(encodedJwt)
                .getPayload()
                .get(USER_ID_KEY, String.class);
    }

    public String getName(String encodedJwt) {
        return Jwts.parser()
                .json(new JacksonDeserializer<>(objectMapper))
                .verifyWith(signer)
                .build()
                .parseSignedClaims(encodedJwt)
                .getPayload()
                .get(NAME_KEY, String.class);
    }
}
