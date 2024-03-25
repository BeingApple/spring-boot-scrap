package com.yongbi.szsyongbi.member.domain;

import java.time.LocalDateTime;

public record Member(Long id,
                     String userId,
                     String password,
                     String name,
                     String regNo,
                     LocalDateTime createdAt) {
    public Member(String userId,
                  String password,
                  String name,
                  String regNo) {
        this(null, userId, password, name, regNo, LocalDateTime.now());

    }

    public Member encryptPassword(String cipher) {
        return new Member(this.id, this.userId, cipher, name, this.regNo, this.createdAt);
    }

    public Member encryptRegNo(String cipher) {
        return new Member(this.id, this.userId, this.password, this.name, cipher, this.createdAt);
    }
}
