package com.yongbi.szsyongbi.income.application.port.out;

public interface DeleteIncomePort {
    boolean delete(long memberId, int year);

    class FakeDeleteIncomePort implements DeleteIncomePort {
        @Override
        public boolean delete(long memberId, int year) {
            return true;
        }
    }
}
