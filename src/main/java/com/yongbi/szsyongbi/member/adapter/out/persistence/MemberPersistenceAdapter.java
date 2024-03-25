package com.yongbi.szsyongbi.member.adapter.out.persistence;

import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.member.application.port.out.SaveMemberPort;
import com.yongbi.szsyongbi.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberPersistenceAdapter implements ReadMemberPort, SaveMemberPort {
    private final MemberRepository memberRepository;

    public MemberPersistenceAdapter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<Member> read(Long id) {
        final var entity = memberRepository.findById(id);
        return entity.map(MemberEntity::domain);
    }

    @Override
    public Optional<Member> read(String userId) {
        final var entity = memberRepository.findByUserId(userId);
        return entity.map(MemberEntity::domain);
    }

    @Override
    public boolean save(Member member) {
        try {
            final var entity = new MemberEntity(member);
            memberRepository.save(entity);

            return true;
        } catch (RuntimeException ex) {
            log.error("Error on Saving Member : " + member, ex);
            throw new RuntimeException("처리 중 오류가 발생했습니다.");
        }
    }
}
