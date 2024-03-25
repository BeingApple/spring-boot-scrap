package com.yongbi.szsyongbi.deduction.application.port.out;

public interface DeleteDeductionPort {
    boolean delete(long memberId, int year);

    class FakeDeleteDeductionPort implements DeleteDeductionPort{

        @Override
        public boolean delete(long memberId, int year) {
            return true;
        }
    }
}
