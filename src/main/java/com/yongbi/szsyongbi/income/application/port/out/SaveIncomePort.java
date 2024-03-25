package com.yongbi.szsyongbi.income.application.port.out;

import com.yongbi.szsyongbi.income.domain.Income;

public interface SaveIncomePort {
    boolean save(Income income);

    class FakeSaveIncomePort implements SaveIncomePort{
        @Override
        public boolean save(Income income) {
            if (income.memberId() == 3L) {
                throw new RuntimeException("처리 중 오류가 발생했습니다.");
            }

            return true;
        }
    }
}
