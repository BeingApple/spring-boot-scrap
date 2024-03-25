package com.yongbi.szsyongbi.income.adapter.out;

import com.yongbi.szsyongbi.income.adapter.out.persistence.IncomeEntity;
import com.yongbi.szsyongbi.income.adapter.out.persistence.IncomeRepository;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberEntity;
import com.yongbi.szsyongbi.member.adapter.out.persistence.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class IncomeRepositoryTest {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    static void createMember(@Autowired MemberRepository memberRepository) {
        final var memberEntity = new MemberEntity(
                "INCOME-TEST-ID",
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
    void createIncome() {
        final var entity = new IncomeEntity(
                1L,
                getMemberId(),
                2023,
                new BigDecimal("20000000"),
                LocalDateTime.now());

        incomeRepository.save(entity);
    }

    @DisplayName("종합소득정보를 DB에서 회원 아이디와 연도를 통해 읽어올 수 있습니다.")
    @Test
    void findByMemberIdAndYearTest() {
        final var actual = incomeRepository.findByMemberIdAndYear(getMemberId(), 2023).stream().findFirst();

        assertThat(actual.isPresent()).isTrue();
        final var income = actual.get().domain();
        assertThat(income.income().compareTo(new BigDecimal("20000000")) == 0).isTrue();
    }

    @DisplayName("종합소득정보를 DB에서 회원 아이디와 연도를 통해 삭제할 수 있습니다.")
    @Test
    void deleteByMemberIdAndYearTest() {
        final var memberId = getMemberId();
        incomeRepository.deleteByMemberIdAndYear(memberId, 2023);

        final var actual = incomeRepository.findByMemberIdAndYear(memberId, 2023);

        assertThat(actual.isEmpty()).isTrue();
    }

    @AfterAll
    static void deleteMember(@Autowired MemberRepository memberRepository) {
        memberRepository.deleteAll();
    }
}
