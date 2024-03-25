package com.yongbi.szsyongbi.member.application.port.in;

import com.yongbi.szsyongbi.member.domain.Member;
import org.springframework.util.StringUtils;

public record CreateMemberCommand(String userId, String password, String name, String regNo) {
    public void verify() {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId 를 입력해주세요");
        }

        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("password 를 입력해주세요");
        }

        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name 를 입력해주세요");
        }

        if (!StringUtils.hasText(regNo)) {
            throw new IllegalArgumentException("regNo 를 입력해주세요");
        }
    }

    public Member domain() {
        return new Member(this.userId, this.password, this.name, this.regNo);
    }
}
