package com.yongbi.szsyongbi.token.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class TokenFactoryTest {
    @DisplayName("회원의 Access token 을 정상적으로 생성합니다.")
    @Test
    void createAccessTokenTest() {
        final var secretKey = "TEST-LONG-LONG-LONG-LONG-SECRET-KEY";
        final var tokenStore = new TokenFactory(new ObjectMapper(),
                Encoders.BASE64.encode(secretKey.getBytes()),
                "TEST-ISSUER",
                "TEST-SUBJECT");
        final var signer = Keys.hmacShaKeyFor(secretKey.getBytes());
        final var id = 1L;
        final var userId = "testId";
        final var name = "test";
        final var twentyMinutesAfter = Date.from(ZonedDateTime.now().plusMinutes(20L).toInstant());
        final var fortyMinutesAfter = Date.from(ZonedDateTime.now().plusMinutes(40L).toInstant());


        final var actual = tokenStore.newUserAccessToken(id, userId, name, 30);

        try {
            final var jwt = Jwts.parser()
                    .verifyWith(signer)
                    .build()
                    .parseSignedClaims(actual);
            final var claims = jwt.getPayload();
            final var expiration = claims.getExpiration();

            assertThat(new Date().before(expiration)).isTrue();
            assertThat(expiration.after(twentyMinutesAfter)).isTrue();
            assertThat(expiration.before(fortyMinutesAfter)).isTrue();
            assertThat(String.valueOf(id)).isEqualTo(String.valueOf(claims.get("id")));
            assertThat(userId).isEqualTo(claims.get("userId"));
            assertThat(name).isEqualTo(claims.get("name"));

        } catch (RuntimeException ex) {
            fail(ex);
        }
    }
}
