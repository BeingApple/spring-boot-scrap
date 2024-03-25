package com.yongbi.szsyongbi.member.application.port.in;

public interface CheckUserIdDuplicationUseCase {
    boolean check(String userId);
}
