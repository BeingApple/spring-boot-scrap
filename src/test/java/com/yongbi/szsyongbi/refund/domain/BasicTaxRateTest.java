package com.yongbi.szsyongbi.refund.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicTaxRateTest {
    @DisplayName("과세표준 금액에 따라 해당하는 과세표준이 나와야 합니다.")
    @Test
    void getBasicTaxRateTest() {
        Map<BasicTaxRate, Double> map = Map.of(
                BasicTaxRate.LEVEL1, 10000000d,
                BasicTaxRate.LEVEL2, 24500000d,
                BasicTaxRate.LEVEL3, 67200000d,
                BasicTaxRate.LEVEL4, 90000000d,
                BasicTaxRate.LEVEL5, 167200000d,
                BasicTaxRate.LEVEL6, 300001000d,
                BasicTaxRate.LEVEL7, 1000000000d,
                BasicTaxRate.LEVEL8, 1500000000d
        );

        for (final var taxRate : map.keySet()) {
            assertThat(taxRate.equals(BasicTaxRate.getTaxRate(map.get(taxRate)))).isTrue();
        }
    }
}
