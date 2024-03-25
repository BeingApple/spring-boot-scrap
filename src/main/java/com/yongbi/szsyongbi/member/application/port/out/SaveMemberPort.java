package com.yongbi.szsyongbi.member.application.port.out;

import com.yongbi.szsyongbi.member.domain.Member;

public interface SaveMemberPort {
    boolean save(Member member);

    class FakeSaveMemberPort implements SaveMemberPort {

        @Override
        public boolean save(Member member) {
            return !member.userId().equals("failedId");
        }
    }
}
