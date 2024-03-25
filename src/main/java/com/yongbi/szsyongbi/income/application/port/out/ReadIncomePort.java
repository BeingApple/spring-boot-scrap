package com.yongbi.szsyongbi.income.application.port.out;

import com.yongbi.szsyongbi.income.domain.Income;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReadIncomePort {
    Optional<Income> read(long memberId, int year);

    class FakeReadIncomePort implements ReadIncomePort {
        private final Map<Long, Income> map;

        public FakeReadIncomePort(Map<Long, Income> map) {
            this.map = map;
        }

        public FakeReadIncomePort() {
            this.map = createFakeIncome();
        }

        private Map<Long, Income> createFakeIncome() {
            Map<Long, Income> fakeMap = new HashMap<>();
            final var fakeIncomes = this.generateFakeIncome();

            for(final var income : fakeIncomes) {
                fakeMap.put(income.id(), income);
            }

            return fakeMap;
        }

        private List<Income> generateFakeIncome() {
            return List.of(
                    new Income(1L, 2L, 2023, new BigDecimal("20000000"), LocalDateTime.now()),
                    new Income(2L, 3L, 2023, new BigDecimal("20000000"), LocalDateTime.now()),
                    new Income(3L, 5L, 2023, new BigDecimal("20000000"), LocalDateTime.now())
            );
        }

        @Override
        public Optional<Income> read(long memberId, int year) {
            return map.values().stream()
                    .filter(i -> memberId == i.memberId() && year == i.year())
                    .findFirst();
        }
    }
}
