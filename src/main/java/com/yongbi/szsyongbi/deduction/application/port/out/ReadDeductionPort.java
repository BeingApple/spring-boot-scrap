package com.yongbi.szsyongbi.deduction.application.port.out;

import com.yongbi.szsyongbi.deduction.domain.Deduction;
import com.yongbi.szsyongbi.deduction.domain.DeductionType;
import com.yongbi.szsyongbi.income.application.port.out.ReadIncomePort;
import com.yongbi.szsyongbi.income.domain.Income;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ReadDeductionPort {
    List<Deduction> read(long memberId, int year);

    class FakeReadDeductionPort implements ReadDeductionPort {
        private final Map<Long, Deduction> map;

        public FakeReadDeductionPort(Map<Long, Deduction> map) {
            this.map = map;
        }

        public FakeReadDeductionPort() {
            this.map = createFakeDeduction();
        }

        private Map<Long, Deduction> createFakeDeduction() {
            Map<Long, Deduction> fakeMap = new HashMap<>();
            Deduction fakeDeduction = this.generateFakeDeduction();

            fakeMap.put(fakeDeduction.id(), fakeDeduction);
            return fakeMap;
        }

        private Deduction generateFakeDeduction() {
            return new Deduction(
                    1L,
                    2L,
                    DeductionType.TAX,
                    2023,
                    3,
                    new BigDecimal("200000.1"),
                    LocalDateTime.now());
        }


        @Override
        public List<Deduction> read(long memberId, int year) {
            return map.values().stream()
                    .filter(d -> memberId == d.memberId() && year == d.year())
                    .collect(Collectors.toList());
        }
    }
}
