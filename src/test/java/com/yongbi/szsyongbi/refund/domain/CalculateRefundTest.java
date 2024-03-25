package com.yongbi.szsyongbi.refund.domain;

import com.yongbi.szsyongbi.deduction.domain.Deduction;
import com.yongbi.szsyongbi.deduction.domain.DeductionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculateRefundTest {
    private List<RefundExampleAndExpectations> getTestData() {
        final var deductions = List.of(
                new Deduction(0L, DeductionType.NATIONAL_PENSION, 2023, new BigDecimal("2000000")),
                new Deduction(0L, DeductionType.CREDIT_CARD, 2023, new BigDecimal("2000000")),
                new Deduction(0L, DeductionType.TAX, 2023, new BigDecimal("200000"))
        );

        return List.of(
                new RefundExampleAndExpectations(new BigDecimal("18200000"), deductions, new BigDecimal("640000")), //과세표준 1400만원 이하
                new RefundExampleAndExpectations(new BigDecimal("25000000"), deductions, new BigDecimal("1660000")), //과세표준 1400만원 초과 5000만원 이하
                new RefundExampleAndExpectations(new BigDecimal("88000000"), deductions, new BigDecimal("14152000")), //과세표준 5000만원 초과 8800만원 이하
                new RefundExampleAndExpectations(new BigDecimal("120000000"), deductions, new BigDecimal("24890000")), //과세표준 8800만원 초과 1억5천만원 이하
                new RefundExampleAndExpectations(new BigDecimal("220000000"), deductions, new BigDecimal("61864000")), //과세표준 1억5천만원 초과 3억원 이하
                new RefundExampleAndExpectations(new BigDecimal("400000000"), deductions, new BigDecimal("132180000")), //과세표준 3억원 초과 5억원 이하
                new RefundExampleAndExpectations(new BigDecimal("720000000"), deductions, new BigDecimal("264496000")), //과세표준 5억원 초과 10억원 이하
                new RefundExampleAndExpectations(new BigDecimal("2000000000"), deductions, new BigDecimal("831970000")) //과세표준 10억원 초과
        );
    }

    @DisplayName("종합소득과 공제 금액에 따라 계산된 결정세액이 나와야 합니다.")
    @Test
    void calculateRefundTest() {
        final var data = getTestData();

        for (final var example : data) {
            final var calculator = new CalculateRefund(
                    example.totalIncome,
                    example.deductions);

            assertThat(calculator.finalizedTax().compareTo(example.finalizedTax) == 0).isTrue();
        }
    }

    record RefundExampleAndExpectations(BigDecimal totalIncome, List<Deduction> deductions, BigDecimal finalizedTax) { }
}
