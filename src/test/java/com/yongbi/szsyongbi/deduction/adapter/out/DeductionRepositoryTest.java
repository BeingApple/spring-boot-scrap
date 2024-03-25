package com.yongbi.szsyongbi.deduction.adapter.out;

import com.yongbi.szsyongbi.deduction.adapter.out.persistence.DeductionEntity;
import com.yongbi.szsyongbi.deduction.adapter.out.persistence.DeductionRepository;
import com.yongbi.szsyongbi.deduction.domain.DeductionType;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberEntity;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DeductionRepositoryTest {
    @Autowired
    private DeductionRepository deductionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    static void createMember(@Autowired MemberRepository memberRepository) {
        final var memberEntity = new MemberEntity(
                "DEDUCTION-TEST-ID",
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
    void createDeduction() {
        final var entity = new DeductionEntity(
                1L,
                getMemberId(),
                DeductionType.NATIONAL_PENSION,
                2023,
                4,
                new BigDecimal("130000"),
                LocalDateTime.now());

        deductionRepository.save(entity);
    }

    @DisplayName("공제 정보를 DB에서 회원 아이디와 연도를 통해 읽어올 수 있습니다.")
    @Test
    void findByMemberIdAndYearTest() {
        final var actual = deductionRepository.findByMemberIdAndYear(getMemberId(), 2023).stream().findFirst();

        assertThat(actual.isPresent()).isTrue();
        final var deduction = actual.get().domain();
        assertThat(deduction.type()).isEqualTo(DeductionType.NATIONAL_PENSION);
        assertThat(deduction.deduction().compareTo(new BigDecimal("130000")) == 0).isTrue();
    }

    @DisplayName("공제 정보를 DB에서 회원 아이디와 연도를 통해 삭제할 수 있습니다.")
    @Test
    void deleteByMemberIdAndYearTest() {
        final var memberId = getMemberId();
        deductionRepository.deleteByMemberIdAndYear(memberId, 2023);

        final var actual = deductionRepository.findByMemberIdAndYear(memberId, 2023);

        assertThat(actual.isEmpty()).isTrue();
    }

    @AfterAll
    static void deleteMember(@Autowired MemberRepository memberRepository) {
        memberRepository.deleteAll();
    }
}
