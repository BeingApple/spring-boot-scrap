package com.yongbi.szsyongbi.scrap.application.port.out;

import com.yongbi.szsyongbi.scrap.adapter.out.http.payload.ScrapData;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public interface RequestScrapPort {
    ScrapData get(String name, String regNo);

    class FakeRequestScrapPort implements RequestScrapPort {
        private ScrapData createFakeScrapData() {
            final var deduction = new ScrapData.IncomeDeduction(
                    new BigDecimal("100000"),
                    Map.of(YearMonth.of(2023, 4), new BigDecimal("200000")),
                    Map.of(YearMonth.of(2023, 4), new BigDecimal("200000"))
            );

            return new ScrapData(
                    "테스트",
                    new BigDecimal("20000000"),
                    deduction
            );
        }

        @Override
        public ScrapData get(String name, String regNo) {
            return createFakeScrapData();
        }
    }
}
