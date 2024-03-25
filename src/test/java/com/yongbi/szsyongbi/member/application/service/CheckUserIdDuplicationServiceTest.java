package com.yongbi.szsyongbi.member.application.service;

import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckUserIdDuplicationServiceTest {
    @Test
    @DisplayName("중복된 회원 아이디는 false 를 반환해야 합니다.")
    void userIdDuplicationTest() {
        final var sut = new CheckUserIdDuplicationService(new ReadMemberPort.FakeReadMemberPort());
        final var check = sut.check("testId");

        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("중복되지 않은 아이디는 true 를 반환해야 합니다.")
    void userIdNotDuplicationTest() {
        final var sut = new CheckUserIdDuplicationService(new ReadMemberPort.FakeReadMemberPort());
        final var check = sut.check("newId");

        assertThat(check).isTrue();
    }
}
