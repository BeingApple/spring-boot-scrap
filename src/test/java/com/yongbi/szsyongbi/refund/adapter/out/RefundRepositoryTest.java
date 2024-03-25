package com.yongbi.szsyongbi.refund.adapter.out;

import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberEntity;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberRepository;
import com.yongbi.szsyongbi.refund.adapter.out.persistence.RefundEntity;
import com.yongbi.szsyongbi.refund.adapter.out.persistence.RefundRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RefundRepositoryTest {
    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    static void createMember(@Autowired MemberRepository memberRepository) {
        final var memberEntity = new MemberEntity(
                "REFUND-TEST-ID",
                "",
                "동탁",
                "",
                LocalDateTime.now());
        memberRepository.save(memberEntity);
    }

    private long getMemberId() {
        final var list = memberRepository.findAll();
        return list.stream().findAny().map(MemberEntity::getId).orElse(1L);
    }

    @BeforeEach
    void createRefund() {
        final var entity = new RefundEntity(
                1L,
                getMemberId(),
                2023,
                new BigDecimal("690000"),
                LocalDateTime.now());

        refundRepository.save(entity);
    }

    @DisplayName("결정세액 정보를 DB에서 회원 아이디와 연도를 통해 읽어올 수 있습니다.")
    @Test
    void findByMemberIdAndYearTest() {
        final var actual = refundRepository.findByMemberIdAndYear(getMemberId(), 2023).stream().findFirst();

        assertThat(actual.isPresent()).isTrue();
        final var refund = actual.get().domain();
        assertThat(refund.refund().compareTo(new BigDecimal("690000")) == 0).isTrue();
    }

    @DisplayName("결정세액 정보를 DB에서 회원 아이디와 연도를 통해 삭제할 수 있습니다.")
    @Test
    void deleteByMemberIdAndYearTest() {
        final var memberId = getMemberId();
        refundRepository.deleteByMemberIdAndYear(memberId, 2023);

        final var actual = refundRepository.findByMemberIdAndYear(memberId, 2023);

        assertThat(actual.isEmpty()).isTrue();
    }

    @AfterAll
    static void deleteMember(@Autowired MemberRepository memberRepository) {
        memberRepository.deleteAll();
    }
}
