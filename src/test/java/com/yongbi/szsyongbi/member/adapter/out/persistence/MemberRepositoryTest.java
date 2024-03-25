package com.yongbi.szsyongbi.member.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("회원 정보를 DB에서 유저 아이디를 통해 읽어올 수 있습니다.")
    @Test
    void findByUserIdTest() {
        final var userId = "testId";
        final var password = "testPassword";
        final var entity = new MemberEntity(
                userId,
                passwordEncoder.encode(password),
                "동탁",
                "",
                LocalDateTime.now());
        memberRepository.save(entity);

        final var actual = memberRepository.findByUserId("testId");

        assertThat(actual.isPresent()).isTrue();
        final var member = actual.get().domain();
        assertThat(member.userId()).isEqualTo(userId);
        assertThat(passwordEncoder.matches(password, member.password())).isTrue();

        memberRepository.deleteAll();
    }
}
