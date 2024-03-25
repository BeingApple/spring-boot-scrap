package com.yongbi.szsyongbi.member.application.service;

import com.yongbi.szsyongbi.member.application.port.in.CheckUserIdDuplicationUseCase;
import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import org.springframework.stereotype.Service;

@Service
public class CheckUserIdDuplicationService implements CheckUserIdDuplicationUseCase {
    private final ReadMemberPort readMemberPort;

    public CheckUserIdDuplicationService(ReadMemberPort readMemberPort) {
        this.readMemberPort = readMemberPort;
    }

    @Override
    public boolean check(String userId) {
        return readMemberPort.read(userId).isEmpty();
    }
}
