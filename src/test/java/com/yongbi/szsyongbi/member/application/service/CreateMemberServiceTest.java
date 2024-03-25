package com.yongbi.szsyongbi.member.application.service;

import com.yongbi.szsyongbi.aes.application.service.AESService;
import com.yongbi.szsyongbi.member.application.port.in.CreateMemberCommand;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.member.application.port.out.SaveMemberPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateMemberServiceTest {
    private CreateMemberCommand invalidMemberCommand() {
        return new CreateMemberCommand("testId",
                new BCryptPasswordEncoder().encode("password"),
                "동탁",
                "890601-2455116");
    }

    @Test
    @DisplayName("정해진 유저 외에 회원가입을 시도하면, 예외가 발생되어야 합니다.")
    void invalidMemberCreateTest() {
        final var fakeReadMemberPort = new ReadMemberPort.FakeReadMemberPort();
        final var service = new CreateMemberService(
                new CheckUserIdDuplicationService(fakeReadMemberPort),
                new SaveMemberPort.FakeSaveMemberPort(),
                new BCryptPasswordEncoder(),
                new AESService("key")
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.create(invalidMemberCommand())).getMessage();

        assertThat(message).contains("가입할 수 없는 사용자입니다.");
    }

    private CreateMemberCommand validMemberCommand() {
        return new CreateMemberCommand("createId",
                new BCryptPasswordEncoder().encode("password"),
                "관우",
                "681108-1582816");
    }

    @Test
    @DisplayName("정해진 유저가 회원가입을 시도하면, 정상적으로 생성되어야 합니다.")
    void validMemberCreateTest() {
        final var fakeReadMemberPort = new ReadMemberPort.FakeReadMemberPort();
        final var service = new CreateMemberService(
                new CheckUserIdDuplicationService(fakeReadMemberPort),
                new SaveMemberPort.FakeSaveMemberPort(),
                new BCryptPasswordEncoder(),
                new AESService("key")
        );

        final var result = service.create(validMemberCommand());

        assertThat(result).isTrue();
    }

    private CreateMemberCommand verifyMemberCommand() {
        return new CreateMemberCommand("",
                new BCryptPasswordEncoder().encode("password"),
                "관우",
                "681108-1582816");
    }

    @Test
    @DisplayName("필수 입력 Field가 누락되었을 경우, 예외가 발생되어야 합니다.")
    void fieldValidationTest() {
        final var fakeReadMemberPort = new ReadMemberPort.FakeReadMemberPort();
        final var service = new CreateMemberService(
                new CheckUserIdDuplicationService(fakeReadMemberPort),
                new SaveMemberPort.FakeSaveMemberPort(),
                new BCryptPasswordEncoder(),
                new AESService("key")
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.create(verifyMemberCommand())).getMessage();

        assertThat(message).contains("를 입력해주세요");
    }

    private CreateMemberCommand failMemberCommand() {
        return new CreateMemberCommand("failedId",
                new BCryptPasswordEncoder().encode("password"),
                "관우",
                "681108-1582816");
    }

    @Test
    @DisplayName("회원가입에 실패하면 false를 반환해야 합니다.")
    void failMemberCreateTest() {
        final var fakeReadMemberPort = new ReadMemberPort.FakeReadMemberPort();
        final var service = new CreateMemberService(
                new CheckUserIdDuplicationService(fakeReadMemberPort),
                new SaveMemberPort.FakeSaveMemberPort(),
                new BCryptPasswordEncoder(),
                new AESService("key")
        );

        final var result = service.create(failMemberCommand());

        assertThat(result).isFalse();
    }

    private CreateMemberCommand duplicatedMemberCommand() {
        return new CreateMemberCommand("testId",
                new BCryptPasswordEncoder().encode("password"),
                "관우",
                "681108-1582816");
    }

    @Test
    @DisplayName("동일한 아이디의 유저가 등록되어있는 경우, 예외가 발생되어야 합니다.")
    void duplicatedMemberCreateTest() {
        final var fakeReadMemberPort = new ReadMemberPort.FakeReadMemberPort();
        final var service = new CreateMemberService(
                new CheckUserIdDuplicationService(fakeReadMemberPort),
                new SaveMemberPort.FakeSaveMemberPort(),
                new BCryptPasswordEncoder(),
                new AESService("key")
        );

        final var message = assertThrows(IllegalArgumentException.class,
                () -> service.create(duplicatedMemberCommand())).getMessage();

        assertThat(message).contains("이미 사용 중인 아이디입니다.");
    }
}
