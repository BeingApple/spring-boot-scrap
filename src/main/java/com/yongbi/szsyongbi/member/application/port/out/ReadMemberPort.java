package com.yongbi.szsyongbi.member.application.port.out;

import com.yongbi.szsyongbi.aes.application.service.AESService;
import com.yongbi.szsyongbi.member.domain.Member;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReadMemberPort {
    Optional<Member> read(Long id);
    Optional<Member> read(String userId);

    class FakeReadMemberPort implements ReadMemberPort {
        private final Map<Long, Member> map;

        public FakeReadMemberPort(Map<Long, Member> map) {
            this.map = map;
        }

        public FakeReadMemberPort() {
            this.map = this.createFakeMember();
        }

        private Map<Long, Member> createFakeMember() {
            final var map = new HashMap<Long, Member>();
            final var fakeMembers = generateFakeMembers();

            for (final var m : fakeMembers) {
                map.put(m.id(), m);
            }

            return map;
        }

        private List<Member> generateFakeMembers() {
            return List.of(
                    new Member(1L,
                            "testId",
                            "",
                            "테스트",
                            new AESService("key").tryEncryptAES256("921108-1582816"),
                            LocalDateTime.now()),
                    new Member(2L,
                            "testId1",
                            "",
                            "테스트",
                            new AESService("key").tryEncryptAES256("921108-1582816"),
                            LocalDateTime.now()),
                    new Member(3L,
                            "testId3",
                            "",
                            "테스트",
                            new AESService("key").tryEncryptAES256("921108-1582816"),
                            LocalDateTime.now()),
                    new Member(4L,
                            "testId4",
                            "",
                            "테스트",
                            new AESService("key").tryEncryptAES256("921108-1582816"),
                            LocalDateTime.now()),
                    new Member(5L,
                            "testId5",
                            "",
                            "테스트",
                            new AESService("key").tryEncryptAES256("921108-1582816"),
                            LocalDateTime.now()),
                    new Member(15L,
                            "testId2",
                            "",
                            "테스트",
                            "",
                            LocalDateTime.now())
            );
        }

        @Override
        public Optional<Member> read(Long id) {
            return Optional.ofNullable(this.map.get(id));
        }

        @Override
        public Optional<Member> read(String userId) {
            return map.values().stream()
                    .filter(m -> userId.equals(m.userId()))
                    .findFirst();
        }
    }
}
