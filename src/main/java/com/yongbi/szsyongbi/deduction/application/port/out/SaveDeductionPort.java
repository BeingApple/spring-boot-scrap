package com.yongbi.szsyongbi.deduction.application.port.out;

import com.yongbi.szsyongbi.deduction.domain.Deduction;

import java.util.Collection;

public interface SaveDeductionPort {
    boolean save(Deduction deduction);
    boolean saveAll(Collection<Deduction> deductions);

    class FakeSaveDeductionPort implements SaveDeductionPort {

        @Override
        public boolean save(Deduction deduction) {
            if (deduction.memberId() == 4L) {
                throw new RuntimeException("처리 중 오류가 발생했습니다.");
            }

            return true;
        }

        @Override
        public boolean saveAll(Collection<Deduction> deductions) {
            if (deductions.stream().anyMatch(d -> d.memberId() == 4L)) {
                throw new RuntimeException("처리 중 오류가 발생했습니다.");
            }

            return true;
        }
    }
}
